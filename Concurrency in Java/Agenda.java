/**
 * Agenda is a monitor that contains two synchronized methods to be called by queue process.
 */

/**
 * @author Ziqi Jia
 * ziqij@student.unimleb.edu.au
 * 693241
 *
 */
import java.util.ArrayList;

public class Agenda extends Thread {

	private String name;
	/* Assume the max number of quests is 200 */
	static final int MAX_QUESTS = 200;

	/* An arraylist to store the quests */
	protected ArrayList<Quest> quests = new ArrayList<>();

	public Agenda(String name) {
		this.name = name;

	}

	/* Add quests to new agenda. */
	public synchronized void addNew(Quest quest) {
		try {
			while (quests.size() >= MAX_QUESTS) {
				wait();
			}
			quests.add(quest);
			System.out.printf("Quest %d added to New Agenda.\n", quests.indexOf(quest) + 1);
			notifyAll();

		} catch (InterruptedException ex) {
		}

	}

	/*
	 * Only when the agenda is not empty, remove quests from agenda, otherwise the
	 * process need to wait.
	 */
	public synchronized void removeComplete() {

		try {
			while (quests.isEmpty()) {
				wait();
			}

			for (int i = 0; i < quests.size(); i++) {
				System.out.printf(quests.get(i).toString() + " removed from Complete Agenda.\n");
				quests.remove(i);
				notifyAll();
			}

		} catch (InterruptedException ex) {
		}

	}

	/* Add quests to complete agenda. */
	public synchronized void addComplete(Quest quest) {
		try {
			while (quests.size() >= MAX_QUESTS) {
				wait();
			}
			quests.add(quest);
			notifyAll();

		} catch (InterruptedException ex) {
		}

	}

}
