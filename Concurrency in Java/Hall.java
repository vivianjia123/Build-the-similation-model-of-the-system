/**
 * Hall is a monitor that contains synchronized functions to be called by Knight and King.
 */

/**
 * @author Ziqi Jia
 * ziqij@student.unimleb.edu.au
 * 693241
 *
 */
import java.util.ArrayList;

public class Hall extends Thread {

	private String name;
	/* An arraylist to store the knights who are in the hall */
	ArrayList<Knight> knights;
	/* A flag to represent if the meeting is in process */
	volatile boolean meetingProcessing = false;
	/* A flag to represent if King is in the hall */
	volatile boolean hasKingInHall = false;

	volatile Agenda agendaNew;
	volatile Agenda agendaComplete;

	public Hall(String string, Agenda agendaNew, Agenda agendaComplete) {
		super();
		this.name = string;
		this.agendaNew = agendaNew;
		this.agendaComplete = agendaComplete;
		knights = new ArrayList<>();
	}

	/*
	 * when the meeting is not in process, the king can enter the hall, otherwise
	 * the process need to wait.
	 */
	public synchronized void kingEnter() {
		while (meetingProcessing) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		System.out.printf("King Arthur enters the Great Hall.\n");
		hasKingInHall = true;
		notifyAll();
	}

	/*
	 * Only when all knights are sited on the table, King is in the Hall, the
	 * meeting can begin, otherwise the process need to wait.
	 */
	public synchronized void StartMeeting() {
		while (!(this.checkAllSit() && hasKingInHall)) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		System.out.printf("Meeting begins!\n");
		this.meetingProcessing = true;
		notifyAll();
	}

	/*
	 * Only when all knights are stand up from the table, King is in the Hall, and
	 * meeting is in process, the meeting can end, otherwise the process need to
	 * wait.
	 */
	public synchronized void endMeeting() {

		while (!(hasKingInHall && checkAllStand() && meetingProcessing)) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.printf("Meeting ends!\n");
		this.meetingProcessing = false;
		notifyAll();
	}

	/*
	 * When the meeting ends, king can leave the hall, otherwise the process need to
	 * wait.
	 */
	public synchronized void kingLeave() {
		while (meetingProcessing) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		System.out.printf("King Arthur exits the Great Hall.\n");
		hasKingInHall = false;
		notifyAll();
	}

	/*
	 * Only when king is not in the hall, and the knight complete the quest or don't
	 * have any quest, the knight can enter the hall, otherwise the process need to
	 * wait.
	 */
	public synchronized void enterGreatHall(Knight knight) {
		while ((knight.isOnQuest() && !knight.isAvailable()) || hasKingInHall) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		knight.setInHall(true);
		System.out.printf("Knight %d enters Great Hall.\n", knight.getKnightId());
		this.knights.add(knight);
		notifyAll();
	}

	/*
	 * When knight is in the hall, the knight can sit on the table, otherwise the
	 * process need to wait.
	 */
	public synchronized void sitTable(Knight knight) {
		while (!knight.isInHall()) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		knight.setSit(true);
		System.out.printf("Knight %d sits at the Round Table.\n", knight.getKnightId());
		notifyAll();
	}

	/*
	 * When meeting is in process, and the knight complete its quest, the knight can
	 * release the quest, otherwise the process need to wait.
	 */
	public synchronized void releaseQuest(Knight knight) {
		if (knight.quest != null) {
			while (!meetingProcessing || !knight.isAvailable()) {
				try {
					wait();
				} catch (InterruptedException e) {
				}
			}
			System.out.printf("Knight %d releases " + knight.quest.toString() + ".\n", knight.getKnightId());
			agendaComplete.addComplete(knight.quest);
			knight.setOnQuest(false);
			knight.setAvailable(false);
			notifyAll();
		}
	}

	/*
	 * When meeting is in process, and the knight don't have any quest, and there
	 * are new quests waiting to be acquired, the knight can acquire the new quest,
	 * otherwise the process need to wait.
	 */
	public synchronized void acquireQuest(Knight knight) {
		while (agendaNew.quests.isEmpty() || !meetingProcessing || knight.isOnQuest()) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		knight.quest = agendaNew.quests.get(0);
		System.out.printf("Knight %d acquires " + knight.quest.toString() + ".\n", knight.getKnightId());
		agendaNew.quests.remove(0);
		knight.setOnQuest(true);
		knight.setAvailable(false);
		notifyAll();
	}

	/*
	 * When meeting is in process, and the knight acquire the new quest, and the
	 * knight is sitting on the table, the knight can stand up from the table,
	 * otherwise the process need to wait.
	 */
	public synchronized void standUp(Knight knight) {
		while (!meetingProcessing || !knight.isOnQuest() || !knight.isSit()) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		knight.setSit(false);
		System.out.printf("Knight %d stands from the Round Table.\n", knight.getKnightId());
		notifyAll();

	}

	/*
	 * When the king is not in hall, and the knight is standing up, the knight can
	 * leave the hall, otherwise the process need to wait.
	 */
	public synchronized void leaveGreatHall(Knight knight) {
		while (hasKingInHall || knight.isSit()) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		System.out.printf("Knight %d exits from Great Hall.\n", knight.getKnightId());
		knight.setInHall(false);
		knights.remove(knight);
		System.out.printf("Knight %d sets of to complete " + knight.quest.toString() + ".\n", knight.getKnightId());
		notifyAll();
	}

	/*
	 * When the knight is not in the hall, and the knight is having an uncompleted
	 * quest, the knight can complete the quest, otherwise the process need to wait.
	 */
	public synchronized void completeQuest(Knight knight) {
		while (knight.isInHall() || !(knight.isOnQuest() && !knight.isAvailable())) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		knight.setAvailable(true);
		knight.setOnQuest(true);
		knight.quest.completed = true;
		System.out.printf("Knight %d completes " + knight.quest.toString() + ".\n", knight.getKnightId());
		notifyAll();
	}

	/*
	 * A function to check if all knights are sitting on the table.
	 */
	private boolean checkAllSit() {
		boolean allKnightSit = true;
		if (!this.knights.isEmpty()) {
			for (int i = 0; i < this.knights.size(); i++) {
				if (this.knights.get(i).isSit() == true) {
					allKnightSit = true;
				} else {
					return false;
				}
			}
		}
		return allKnightSit;
	}

	/*
	 * A function to check if all knights are standing from the table.
	 */
	private boolean checkAllStand() {
		boolean allKnightsStand = true;
		if (!this.knights.isEmpty()) {
			for (int i = 0; i < this.knights.size(); i++) {
				if (this.knights.get(i).isSit() != true) {
					allKnightsStand = true;
				} else {
					return false;
				}
			}
		}
		return allKnightsStand;
	}
}