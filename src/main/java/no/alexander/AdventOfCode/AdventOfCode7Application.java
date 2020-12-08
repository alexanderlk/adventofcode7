package no.alexander.AdventOfCode;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AdventOfCode7Application implements CommandLineRunner {
	private static Logger LOG = LoggerFactory.getLogger(AdventOfCode7Application.class);

	
	public static void main(String[] args) {
		SpringApplication.run(AdventOfCode7Application.class, args);
	}

	@Override
	public void run(String... args) throws URISyntaxException, IOException {
		URL input = getClass().getClassLoader().getResource("input.txt");
		File file = new File(input.toURI());
		
		List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
		
		Map<String, Bag> bags = new HashMap<>();
		for (String line : lines) {
			parseBag(line, bags);
		}
		
		partOne(bags);
		partTwo(bags);
	}
	
	private void parseBag(String line, Map<String, Bag> bags) {
		String[] parts = line.split(" bags contain ");
		String colour = parts[0];
		
		Bag bag = getOrCreate(colour, bags);
		
		if (parts[1].equals("no other bags.")) {
		} else {
			String[] cps = parts[1].split(", ");
			List<BagCount> bagCounts = bag.getBagCounts();
			
			for (String cp : cps) {
				int firstSpaceIndex = cp.indexOf(" ");
				String count = cp.substring(0, firstSpaceIndex);
				int lastSpaceIndex = cp.lastIndexOf(" ");
				String c = cp.substring(firstSpaceIndex + 1, lastSpaceIndex);
				
				Bag cb = getOrCreate(c, bags);
				bagCounts.add(new BagCount(cb, Integer.valueOf(count)));
			}
		}
	}
	
	private Bag getOrCreate(String colour, Map<String, Bag> bags) {
		Bag bag = bags.get(colour);
		if ( bag == null) {
			bag = new Bag(colour);
			bags.put(colour, bag);
		}
		return bag;
	}
	
	private void partOne(Map<String, Bag> bags) {
		var count = 0;
		for (Bag bag : bags.values()) {
			if (bag.mayContain("shiny gold")) {
				count++;
			}
		}
		
		LOG.info("Part one - " + count);
	}
	
	private void partTwo(Map<String, Bag> bags) {
		Bag bag = bags.get("shiny gold");
		LOG.info("Part two - " + (bag.getContainedBagCount() - 1));
	}
	
	private class Bag {
		private String colour;
		private List<BagCount> containedBags;

		public Bag(String colour) {
			this.colour = colour;
			containedBags = new ArrayList<>();
		}
		
		public List<BagCount> getBagCounts() {
			return containedBags;
		}
		
		public boolean mayContain(String colour) {
			for (BagCount bc : containedBags) {
				Bag bag = bc.getBag();
				if (bag.colour.equals(colour)) {
					return true;
				}
				
				if (bag.mayContain(colour)) {
					return true;
				}
			}
			return false;
		}
		
		public int getContainedBagCount() {
			var contained = 1;
			
			for (BagCount bc : containedBags) {
				Bag bag = bc.getBag();
				var c = bag.getContainedBagCount();
				contained += (c * bc.getCount());
			}
			
			return contained;
		}
		
		@Override
		public int hashCode() {
			return colour.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Bag)) {
				return false;
			}
			Bag that = (Bag) obj;
			return this.colour.equals(that.colour);
		}
	}
	
	private class BagCount {
		private Bag bag;
		private int count;
		
		public BagCount(Bag bag, int count) {
			this.bag = bag;
			this.count = count;
		}
		
		public Bag getBag() {
			return bag;
		}
		
		public int getCount() {
			return count;
		}
	}
	
	
	
}
