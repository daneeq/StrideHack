import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

public class Main_Collapse {
	
	public static final float COLLAPSE_DISTANCE = 50.0f; // in meters

	public static abstract class Point {
		public abstract float getLat();
		public abstract float getLng();
		public abstract float getSeverity();
		public abstract int getImportance();
		
		public float distFrom(Point other) {
			float lat1 = this.getLat();
			float lng1 = this.getLng();
			float lat2 = other.getLat();
			float lng2 = other.getLng();
						
		    double earthRadius = 3958.75;
		    double dLat = Math.toRadians(lat2-lat1);
		    double dLng = Math.toRadians(lng2-lng1);
		    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
		               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
		               Math.sin(dLng/2) * Math.sin(dLng/2);
		    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		    double dist = earthRadius * c;

		    int meterConversion = 1609;

		    return new Float(dist * meterConversion).floatValue();
	    }
		
		public String toString() {
			return "map.drawCircle({lat: " + getLat() + ", lng: " + getLng() + ", fillColor: \"" +
		
			(getSeverity() >= 3 ? "#ff0000" : "#ffffff") 
		
			+ "\", radius: " + (getImportance() * 20) + "});";
			
		}
	}
	
	public static class BasicPoint extends Point {
		public float lat, lng, sev;
		
		public BasicPoint(float lat, float lng, float sev) {
			this.lat = lat;
			this.lng = lng;
			this.sev = sev;
		}
		
		public float getLat() { return lat; }
		public float getLng() { return lng; }
		public float getSeverity() { return sev; }
		public int getImportance() { return 1; }
		
	}
	
	public static class MergedPoint extends Point {
		BasicPoint[] basicPoints;
		public float lat, lng, sev;
		
		public MergedPoint(List<BasicPoint> points) {
			basicPoints = points.toArray(new BasicPoint[0]);
			
			lat = lng = sev = 0.0f;
			for (BasicPoint p : points) { 
				lat += p.lat;
				lng += p.lng;
				sev += p.sev;
			}
			lat /= basicPoints.length;
			lng /= basicPoints.length;
			sev /= basicPoints.length;
		}
		
		public float getLat() { return lat; }
		public float getLng() { return lng; }
		public float getSeverity() { return sev; }
		public int getImportance() { return basicPoints.length; }
	}
	
	public static void main(String[] args) throws IOException {
		
		List<BasicPoint> points = new ArrayList<BasicPoint>();
		
		// LOAD DATA
		
		CSVReader reader = new CSVReader(new FileReader(args[0]));
	    String [] nextLine;
	    boolean first = true;
	    while ((nextLine = reader.readNext()) != null) {
	    	if (first) {
	    		first = false;
	    		continue;
	    	}
	    	points.add(new BasicPoint(Float.parseFloat(nextLine[4]), Float.parseFloat(nextLine[3]), Float.parseFloat(nextLine[6])));
	    }

	    // PROCESS
	    
	    List<Point> mergedPoints = new ArrayList<Point>();
	    
	    while (!points.isEmpty()) {
	    	BasicPoint p1 = points.get(points.size() - 1);
	    	
	    	List<BasicPoint> similar = new ArrayList<BasicPoint>();
	    	similar.add(p1);
	    		    	
	    	for (BasicPoint p2 : points) {
	    		if (p1 != p2 && p1.distFrom(p2) < COLLAPSE_DISTANCE)
	    			similar.add(p2);
	    	}
	    	
	    	for (Point p : similar)
	    		points.remove(p);
	    	
	    	if (similar.size() > 1)
	    		mergedPoints.add(new MergedPoint(similar));
	    	else
	    		mergedPoints.add(p1);
	    }
	    
	    // SORT
	    
	    Collections.sort(mergedPoints, new Comparator<Point>() {
			@Override
			public int compare(Point o1, Point o2) {
				return Integer.compare(o1.getImportance(), o2.getImportance());
			}
		});
	    
	    // WRITE OUT
	    
	    for (Point p : mergedPoints) {
	    	if (p.getImportance() >= 3 || p.getSeverity() > 3.0f)
	    		System.out.println(p);
	    }
	    
	    System.out.println("TOTAL NUMBER: " + mergedPoints.size());
	    
	    reader.close();
	}

}
