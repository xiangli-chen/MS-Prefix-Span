package MSPrefixSpan;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xiangli chen
 * 
 */
public class MSPrefixSpan {
	/**
	 * @author xiangli chen
	 * 
	 */
	Map<Integer, Double> MIS = getItemsMIS();

	ArrayList<Sequence> S = getSequences(MIS);

	double SDC;

	int size = S.size();

	Map<Integer, Item> items = getMapItems(S);

	Comparator comItemMIS = new Comparator<Item>() {

		public int compare(Item o1, Item o2) {
			// TODO Auto-generated method stub
			if (o1.mis > o2.mis)
				return 1;
			if (o1.mis == o2.mis && o1.frequence > o2.frequence)
				return 1;
			if (o1.mis < o2.mis)
				return -1;
			return 0;
		}
	};

	Comparator comItemFreq = new Comparator<Item>() {

		public int compare(Item o1, Item o2) {
			// TODO Auto-generated method stub
			if (o1.frequence > o2.frequence)
				return 1;
			if (o1.frequence < o2.frequence)
				return -1;
			return 0;
		}
	};

	Comparator comItemKey = new Comparator<Item>() {

		public int compare(Item o1, Item o2) {
			// TODO Auto-generated method stub
			if (o1.key > o2.key)
				return 1;
			if (o1.key < o2.key)
				return -1;
			return 0;
		}
	};

	class Item {
		int key;

		double mis;// minimum

		int frequence;

		boolean flag;// true:frequent false:unfrequent

		Item() {
			this.key = 0;
			this.mis = 0;
			this.frequence = 0;
			this.flag = false;
		}

		Item(int arg0, double arg1, int arg2, boolean arg3) {
			this.key = arg0;
			this.mis = arg1;
			this.frequence = arg2;
			this.flag = arg3;
		}

		public Item copyItem() {
			Item item = new Item();
			item.flag = this.flag;
			item.frequence = this.frequence;
			item.flag = this.flag;
			item.key = this.key;
			return item;
		}
	}

	/**
	 * @author xiangli chen
	 * 
	 */
	class Sequence {
		ArrayList<ArrayList<Integer>> sequence;

		double mis;

		int frequence;

		int maxSup;

		int minSup;

		Sequence() {
			this.sequence = new ArrayList<ArrayList<Integer>>();
			this.mis = 0;
			this.frequence = 0;
			this.maxSup = 0;
			this.minSup = 0;
		}

		Sequence(Item item) {
			this.sequence = new ArrayList<ArrayList<Integer>>();
			ArrayList<Integer> subSequence = new ArrayList<Integer>();
			subSequence.add(item.key);
			this.sequence.add(subSequence);
			this.mis = item.mis;
			this.frequence = item.frequence;
			this.maxSup = item.frequence;
			this.minSup = item.frequence;
		}

		Sequence(ArrayList<ArrayList<Integer>> sequence) {
			this.sequence = new ArrayList<ArrayList<Integer>>();
			ArrayList<Integer> subSequence = null;
			Iterator<ArrayList<Integer>> iteALI = null;
			Iterator<Integer> iteI = null;
			iteALI = sequence.iterator();
			while (iteALI.hasNext()) {
				subSequence = new ArrayList<Integer>();
				iteI = iteALI.next().iterator();
				while (iteI.hasNext()) {
					subSequence.add(iteI.next());
				}
				this.sequence.add(subSequence);
			}
			this.mis = 0;
			this.frequence = 0;
			this.maxSup = 0;
			this.minSup = 0;
		}

		Sequence(Sequence s) {
			this.sequence = new Sequence(s.sequence).sequence;
			this.mis = s.mis;
			this.frequence = s.frequence;
			this.maxSup = s.maxSup;
			this.minSup = s.minSup;
		}

		public Sequence copySequence() {
			Sequence s = new Sequence();
			ArrayList<Integer> subSequence = null;
			Iterator<ArrayList<Integer>> iteALI = null;
			Iterator<Integer> iteI = null;
			iteALI = this.sequence.iterator();
			while (iteALI.hasNext()) {
				subSequence = new ArrayList<Integer>();
				iteI = iteALI.next().iterator();
				while (iteI.hasNext()) {
					subSequence.add(iteI.next());
				}
				s.sequence.add(subSequence);
			}
			s.mis = this.mis;
			s.frequence = this.frequence;
			s.maxSup = this.maxSup;
			s.minSup = this.minSup;
			return s;
		}

		public boolean contain_item(int item) {
			Iterator<ArrayList<Integer>> ite1 = sequence.iterator();
			Iterator<Integer> ite2 = null;
			while (ite1.hasNext()) {
				ite2 = ite1.next().iterator();
				while (ite2.hasNext()) {
					if (ite2.next() == item)
						return true;
				}
			}
			return false;
		}

		// @SuppressWarnings("unchecked")
		public ArrayList<ArrayList<Integer>> sequenceProject(
				ArrayList<Integer> prefix) {
			// TODO Auto-generated method stub
			ArrayList<ArrayList<Integer>> sequence = null;
			ArrayList<Integer> temSubSequence = null;
			ArrayList<Integer> temItems = null;
			Iterator<ArrayList<Integer>> iteALI = this.sequence.iterator();
			sequence = new Sequence(this.sequence).sequence;
			iteALI = sequence.iterator();
			boolean seperate = (prefix.size() == 1) ? true : false;
			temItems = iteALI.next();
			if (temItems.get(0) == 0) {// {_,70,80}
				if (!seperate) {// {30,40}
					temItems.remove(0);
					ArrayList<Integer> temPrefix = new ArrayList<Integer>();
					temPrefix.add(prefix.get(prefix.size() - 1));
					temSubSequence = subSequenceProject(temItems, temPrefix);
					iteALI.remove();
					if (temSubSequence != null) {
						if (temSubSequence.isEmpty()) {
							return sequence;
						} else {
							sequence.add(0, temSubSequence);
							return sequence;
						}
					}
				} else
					iteALI.remove();// {30}{x}
			} else
				iteALI = sequence.iterator();
			while (iteALI.hasNext()) {
				temItems = iteALI.next();
				temSubSequence = subSequenceProject(temItems, prefix);
				iteALI.remove();
				if (temSubSequence != null) {
					if (temSubSequence.isEmpty()) {
						return sequence;
					} else {
						sequence.add(0, temSubSequence);
						return sequence;
					}
				}
			}
			return null;
		}
	}//

	void print(Object o) {
		System.out.println(o);
	}

	private Map<Integer, Item> getMapItems(ArrayList<Sequence> S) {
		// TODO Auto-generated method stub
		LinkedList<Item> items = getItems(S);
		Map<Integer, Item> mapItems = new HashMap<Integer, Item>();
		Item item = null;
		Iterator<Item> iteI = items.iterator();
		while (iteI.hasNext()) {
			item = iteI.next();
			mapItems.put(item.key, item);
		}
		return mapItems;
	}

	public ArrayList<Integer> subSequenceProject(
			ArrayList<Integer> subSequence, ArrayList<Integer> prefix) {
		if (subSequence.size() < prefix.size())
			return null;
		ArrayList<Integer> temSubSequence = new ArrayList<Integer>();
		Iterator<Integer> iteI = subSequence.iterator();
		while (iteI.hasNext()) {
			temSubSequence.add(iteI.next());
		}
		for (int i = 0; i < prefix.size();) {
			while (!temSubSequence.isEmpty()) {
				if (prefix.get(i) == temSubSequence.get(0)) {
					i++;
					temSubSequence.remove(0);
					break;
				} else {
					temSubSequence.remove(0);
				}
			}
			if (i < prefix.size() && temSubSequence.isEmpty())
				return null;
		}
		if (!temSubSequence.isEmpty()) {
			temSubSequence.add(0, 0);
		}
		return temSubSequence;
	}

	// finished
	/*
	 * private double getSequenceMIS(Map<Integer, Double> MIS, HashSet<Integer>
	 * set) { // TODO Auto-generated method stub Iterator<Integer> ite =
	 * set.iterator(); double mis = 1.01; double tem = 0; while (ite.hasNext()) {
	 * tem = MIS.get(ite.next()); mis = mis > tem ? tem : mis; } return mis; }
	 */

	// 
	private ArrayList<Sequence> getSequences(Map<Integer, Double> MIS) {
		// open file
		ArrayList<Sequence> S = new ArrayList<Sequence>();
		Sequence s = null;
		ArrayList<Integer> array = null;
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader("./data2/data-2.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String ss = new String();
		Pattern p1 = Pattern.compile("[\\{][^\\}]*[\\}]");
		Pattern p2 = Pattern.compile("\\d{1,}");
		Matcher m1 = null;
		Matcher m2 = null;
		try {
			while ((ss = in.readLine()) != null) {
				m1 = p1.matcher(ss);
				s = new Sequence();
				while (m1.find()) {
					array = new ArrayList<Integer>();
					m2 = p2.matcher(m1.group());
					while (m2.find()) {
						array.add(Integer.parseInt(m2.group()));
					}
					s.sequence.add(array);
				}
				S.add(s);
			}
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//print_sequences(S);
		//print(S.size());
		return S;
	}

	// ?
	private Map<Integer, Double> getItemsMIS() {
		// TODO Auto-generated method stub
		Map<Integer, Double> MIS = new HashMap<Integer, Double>();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader("./data2/para2-1.txt"));
			String ss = new String();
			Pattern p1 = Pattern.compile("\\(\\d{1,}");
			Pattern p2 = Pattern.compile("\\d{1,}\\.\\d{1,}");
			Pattern p3 = Pattern.compile("MIS.*");
			Pattern p4 = Pattern.compile("SDC.*");
			int key = 0;
			double mis = 0;
			Matcher m1 = null;

			while ((ss = in.readLine()) != null) {
				m1 = p3.matcher(ss);
				if (m1.matches()) {
					m1 = p1.matcher(ss);
					if (m1.find())
						key = Integer.parseInt(m1.group().substring(1));
					m1 = p2.matcher(ss);
					if (m1.find()) {
						mis = Double.parseDouble(m1.group());
						MIS.put(key, mis);
					}
				} else {
					m1 = p4.matcher(ss);
					if (m1.matches()) {
						m1 = p2.matcher(ss);
						if (m1.find())
							SDC = Double.parseDouble(m1.group());
					}
				}
			}
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// print(MIS);
		return MIS;
	}

	// ?

	@SuppressWarnings("unused")
	// finished
	private void print_sequences(List<Sequence> sequences) {
		String ss = new String();
		for (int i = 0; i < sequences.size(); i++) {
			ss = ss + '<';
			for (int j = 0; j < sequences.get(i).sequence.size(); j++) {
				ss = ss + '{';
				for (int k = 0; k < sequences.get(i).sequence.get(j).size(); k++) {
					if (k != 0)
						ss = ss + ',';
					ss = ss
							+ sequences.get(i).sequence.get(j).get(k)
									.toString();
				}
				ss = ss + '}';
			}
			ss = ss + ">\n";
		}
		print(ss);
	}

	// finished
	@SuppressWarnings("unchecked")
	private LinkedList<Item> getItems(ArrayList<Sequence> S) {
		// TODO Auto-generated method stub
		Map<Integer, Item> mapItems = new LinkedHashMap<Integer, Item>();
		LinkedList<Item> Items = new LinkedList<Item>();
		Iterator<Sequence> ite1 = S.iterator();
		Iterator<ArrayList<Integer>> ite2 = null;
		Iterator<Integer> ite3 = null;
		int tem = 0;
		double mis = 0;
		Item tem2 = null;
		ArrayList<Integer> tem3 = new ArrayList<Integer>();
		while (ite1.hasNext()) {
			ite2 = ite1.next().sequence.iterator();
			while (ite2.hasNext()) {
				ite3 = ite2.next().iterator();
				while (ite3.hasNext()) {
					tem = ite3.next();
					if (!mapItems.containsKey(tem)) {
						if (MIS.get(tem) == null)
							mis = 0;
						else
							mis = MIS.get(tem);
						mapItems.put(tem, new Item(tem, mis, 1, false));
						tem3.add(tem);
					}// if
					else if (!tem3.contains(tem)) {
						tem3.add(tem);
						tem2 = mapItems.get(tem);
						tem2.frequence++;
					}// else if
				}// while(ite3.hasNext())
			}// while(ite2.hasNext())
			tem3.clear();
		}// while(ite1.hasNext())
		Items.addAll(mapItems.values());
		return Items;
	}

	// finished
	private void updateSequences(ArrayList<Sequence> S,
			ArrayList<Integer> delItems) {
		// TODO Auto-generated method stub
		Iterator<Sequence> iteSequence = S.iterator();
		ArrayList<ArrayList<Integer>> sequence = null;
		Iterator<ArrayList<Integer>> iteALI = null;
		ArrayList<Integer> subSequence = null;
		Iterator<Integer> iteI = null;
		while (iteSequence.hasNext()) {
			sequence = iteSequence.next().sequence;
			iteALI = sequence.iterator();
			while (iteALI.hasNext()) {
				subSequence = iteALI.next();
				iteI = subSequence.iterator();
				while (iteI.hasNext()) {
					if (delItems.contains(iteI.next())) {
						iteI.remove();
					}
				}
				if (subSequence.isEmpty())
					iteALI.remove();
			}
			if (sequence.isEmpty())
				iteSequence.remove();
		}
	}

	// finished
	private ArrayList<Sequence> copySequences(ArrayList<Sequence> S) {
		ArrayList<Sequence> temS = new ArrayList<Sequence>();
		Iterator<Sequence> ite1 = S.iterator();
		Sequence s = null;
		while (ite1.hasNext()) {
			s = ite1.next().copySequence();
			temS.add(s);
		}
		return temS;
	}

	// ?
	private void construct_fS_PSS(Sequence s, ArrayList<Sequence> S,
			LinkedList<Sequence> fSequences,
			LinkedList<ArrayList<Sequence>> projectedSS, Item item) {
		// TODO Auto-generated method stub
		// get possible x
		ArrayList<Integer> keys = new ArrayList<Integer>();
		LinkedList<Item> items = getItems(S);
		while (!items.isEmpty()) {//get potential items for extending
			if (items.getFirst().key == 0)
				items.removeFirst();
			else if ((double) items.getFirst().frequence / this.size < item.mis)
				items.removeFirst();
			else {
				keys.add(items.getFirst().key);
				items.removeFirst();
			}
		}
		Iterator<Sequence> iteSequence = null;
		Sequence tem_s = null;
		ArrayList<ArrayList<Integer>> sequence = null;
		ArrayList<Integer> prefix = null;
		ArrayList<Sequence> temS = null;
		int key = 0;
		Item temItem = null;
		Sequence temfS = null;
		for (int i = 0; i < keys.size(); i++) {
			// {30}{x}
			temS = new ArrayList<Sequence>();
			temfS = new Sequence(s);
			temfS.frequence = 0;
			prefix = new ArrayList<Integer>();
			key = keys.get(i);
			prefix.add(key);
			temfS.sequence.add(prefix);
			temItem = this.items.get(key);
			if (temItem.frequence > temfS.maxSup
					|| temItem.frequence < temfS.minSup) {// SDC check
				if (temfS.maxSup < temItem.frequence)
					temfS.maxSup = temItem.frequence;
				else
					temfS.minSup = temItem.frequence;
				if ((double) (temfS.maxSup - temfS.minSup) / size > SDC)
					continue;
			}
			iteSequence = S.iterator();
			while (iteSequence.hasNext()) {
				sequence = iteSequence.next().sequenceProject(prefix);
				if (sequence != null) {
					temfS.frequence++;
					if (!sequence.isEmpty()) {
						tem_s = new Sequence(sequence);
						temS.add(tem_s);
					}
				}
			}// while
			if ((double) temfS.frequence / this.size >= item.mis) {
				fSequences.add(temfS);
				projectedSS.add(temS);
			}
			Sequence temfS2 = new Sequence(temfS);//keep the minSup and maxSup
			// {30,x}
			temS = new ArrayList<Sequence>();
			temfS = new Sequence(s);
			temfS.frequence = 0;
			temfS.minSup = temfS2.minSup;
			temfS.maxSup = temfS2.maxSup;
			tem_s = new Sequence(s);
			prefix = tem_s.sequence.get(tem_s.sequence.size() - 1);
			prefix.add(key);
			ArrayList<Integer> temAI = null;
			temAI = temfS.sequence.remove(temfS.sequence.size() - 1);
			temAI.add(key);
			temfS.sequence.add(temAI);
			
			iteSequence = S.iterator();
			while (iteSequence.hasNext()) {
				sequence = iteSequence.next().sequenceProject(prefix);
				if (sequence != null) {
					temfS.frequence++;
					if (!sequence.isEmpty()) {
						tem_s = new Sequence(sequence);
						temS.add(tem_s);
					}
				}
			}// while
			if ((double) temfS.frequence / this.size >= item.mis) {
				fSequences.add(temfS);
				projectedSS.add(temS);
			}
		}// for
	}

	// ?
	private void recursive_r_PrefixSpan(
			LinkedList<ArrayList<Sequence>> projectedSS,
			LinkedList<Sequence> fSequences, ArrayList<Sequence> patterns,
			Item item) {
		// TODO Auto-generated method stub
		if (fSequences.isEmpty())
			return;
		ArrayList<Sequence> temS = null;
		Sequence s = null;
		// print(fSequences.size());
		while (!fSequences.isEmpty()) {
			temS = projectedSS.poll();
			// print_sequences(tem_S);
			s = fSequences.poll();
			if (s.contain_item(item.key)) {
				patterns.add(s);
			}
			if (temS.isEmpty()) {
				continue;
			}
			LinkedList<Sequence> subfSequences = new LinkedList<Sequence>();
			LinkedList<ArrayList<Sequence>> subProjectedSS = new LinkedList<ArrayList<Sequence>>();
			construct_fS_PSS(s, temS, subfSequences, subProjectedSS, item);
			// get an array of extended frequent sequences and their
			// corresponding projected S based on
			// current one
			recursive_r_PrefixSpan(subProjectedSS, subfSequences, patterns,
					item);
		}
		return;
	}

	// ?
	@SuppressWarnings("unchecked")
	private ArrayList<Sequence> r_PrefixSpan(Item item, ArrayList<Sequence> S) {
		// TODO Auto-generated method stub
		Sequence s = new Sequence(item);
		ArrayList<Sequence> patterns = new ArrayList<Sequence>();
		// patterns.add(s);// frequent item
		LinkedList<Sequence> fSequences = new LinkedList<Sequence>();
		fSequences.add(s);
		Iterator<Sequence> iteSequence1 = S.iterator();
		while (iteSequence1.hasNext()) {// get sequences contain current
			// frequent item
			s = iteSequence1.next();
			if (!s.contain_item(item.key))
				iteSequence1.remove();
		}
		// print_sequences(S);
		LinkedList<Item> items = getItems(S);
		Collections.sort(items, comItemFreq);
		Iterator<Item> iteItem = items.iterator();
		Item temItem = null;
		ArrayList<Integer> delItems = new ArrayList<Integer>();
		while (iteItem.hasNext()) {// get frequent prefix
			temItem = iteItem.next();
			if (temItem.frequence < item.frequence
					&& (double) temItem.frequence / this.size < item.mis) {
				iteItem.remove();
				delItems.add(temItem.key);
			} else if (temItem.key != item.key) {
				s = new Sequence(temItem);
				s.maxSup = this.items.get(temItem.key).frequence;
				s.minSup = s.maxSup;
				fSequences.add(s);
			}
		}
		updateSequences(S, delItems);
		// print_sequences(S);
		LinkedList<ArrayList<Sequence>> Projected_SS = new LinkedList<ArrayList<Sequence>>();
		ArrayList<Sequence> temS = null;
		iteSequence1 = fSequences.iterator();
		Iterator<Sequence> iteSequence2 = null;
		ArrayList<ArrayList<Integer>> sequence1 = null;
		ArrayList<ArrayList<Integer>> sequence2 = null;
		while (iteSequence1.hasNext()) {// get projected S for each frequent
			// prefix
			sequence1 = iteSequence1.next().sequence;
			iteSequence2 = S.iterator();
			temS = new ArrayList<Sequence>();
			while (iteSequence2.hasNext()) {
				s = iteSequence2.next();
				sequence2 = s.sequenceProject(sequence1.get(0));
				if (sequence2 != null && !sequence2.isEmpty()) {
					if (sequence1.get(0).get(0) != item.key) {
						for (int i = 0; i < sequence2.size(); i++) {
							if (sequence2.get(i).contains(item.key)) {
								s = new Sequence(sequence2);
								temS.add(s);
							}
						}
					} else {
						s = new Sequence(sequence2);
						temS.add(s);
					}
				}
			}
			// print_sequences(temS);
			Projected_SS.add(temS);
		}
		// print_sequences(fSequences);
		recursive_r_PrefixSpan(Projected_SS, fSequences, patterns, item);
		// print_sequences(patterns);
		return patterns;
	}

	private void outputFsequences(ArrayList<Sequence> patterns) {
		// TODO Auto-generated method stub
		String ss = new String();
		for (int i = 0; i < patterns.size(); i++) {
			ss = ss + '<';
			for (int j = 0; j < patterns.get(i).sequence.size(); j++) {
				ss = ss + '{';
				for (int k = 0; k < patterns.get(i).sequence.get(j).size(); k++) {
					if (k != 0)
						ss = ss + ',';
					ss = ss + patterns.get(i).sequence.get(j).get(k).toString();
				}
				ss = ss + '}';
			}
			ss = ss + "> Frequence:" + patterns.get(i).frequence + "\n";
		}

		try {
			File f = new File("./result.txt");
			;
			if (f.exists()) {
				f.delete();
			}
			FileWriter fw = null;
			fw = new FileWriter("./result.txt");
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(ss);
			bw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// print(ss);

	}

	@SuppressWarnings("unchecked")
	// ?
	void execute() {
		LinkedList<Item> items = getItems(S);
		//Collections.sort(items, comItemKey);
		//print_items(items);
		Collections.sort(items, comItemMIS);// important sort
		Iterator<Item> iteItem = items.iterator();
		Item item1 = null;
		// find out frequent items
		while (iteItem.hasNext()) {
			item1 = iteItem.next();
			if ((double) item1.frequence / this.size >= item1.mis) {
				item1.flag = true;// frequent
			}
		}
		item1 = null;
		Item item2 = null;
		ArrayList<Integer> delItems = new ArrayList<Integer>();
		ArrayList<Integer> delItemsSDC = new ArrayList<Integer>();
		ArrayList<Sequence> patterns = new ArrayList<Sequence>();
		iteItem = items.iterator();
		// Before each of following iterations (frequent item), we have delete
		// all items with lower mis.
		// Lower mis items are either frequent items has been checked or
		// infrequent items.
		// Actually, the frequent item searched in each time has the lowest mis
		// in updated sequences.
		// So, we can furhter delete the items with higher mis but the frequence
		// is unsatisfied.
		// The update of sequences based on the assumptions above is continuous.
		// In addition, we need to delete items violent SDC for each frequent
		// item which is exclusive.
		while (iteItem.hasNext()) {
			item1 = iteItem.next();
			// print(item1.key);
			if (item1.flag == true) {// frequent item
				while (iteItem.hasNext()) {
					item2 = iteItem.next();
					if ((double) item2.frequence / this.size < item1.mis) {
						delItems.add(item2.key);
						iteItem.remove();
					} else if (Math// delete items
							.abs((double) (item2.frequence - item1.frequence)
									/ this.size) > SDC)
						delItemsSDC.add(item2.key);// this is exclusive for
					// each frequent item
				}// while
				if (!delItems.isEmpty()) {
					updateSequences(S, delItems);
					delItems.clear();
				}
				ArrayList<Sequence> temS = null;
				temS = copySequences(S);
				if (!delItemsSDC.isEmpty()) {
					updateSequences(temS, delItemsSDC);
					delItemsSDC.clear();
				}
				//print_sequences(temS);
				patterns.addAll(r_PrefixSpan(item1, temS));
				delItems.add(item1.key);
				iteItem = items.iterator();
				iteItem.next();
				iteItem.remove();
			} else {// delete items with lower mis that current frequent item
				delItems.add(item1.key);
				iteItem.remove();
			}
		}
	   //print_sequences(patterns);
		outputFsequences(patterns);
	}

	@SuppressWarnings("unused")
	private void print_items(LinkedList<Item> items) {
		// TODO Auto-generated method stub
		Iterator<Item> iteI = items.iterator();
		Item item = null;
		while (iteI.hasNext()) {
			item = iteI.next();
			print("key:" + item.key + " " + "frequences:" + item.frequence
					+ " " + "mis:" + item.mis + " " + "flag:" + item.flag);
		}
	}

	// 
	public static void main(String arge[]) {
		MSPrefixSpan MSPS = new MSPrefixSpan();
		MSPS.execute();
	}
}
