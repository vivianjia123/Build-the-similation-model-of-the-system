/**
 * Knight is a important character that to acquires new and releases completed quests, and completes quests.
 */

/**
 * @author Ziqi Jia ziqij@student.unimleb.edu.au 693241
 *
 */
public class Knight extends Thread {

	/* A number to represent the knight id */
	private int id;
	/*
	 * A flag to represent if the knight complete quest and available for the new
	 * quest
	 */
	private volatile boolean isAvailable = false;
	/* A flag to represent if the knight is sitting on the table */
	private volatile boolean isSit = false;
	/* A flag to represent if the knight is in the hall */
	private volatile boolean isInHall = false;
	/* A flag to represent if the knight is on a quest */
	private volatile boolean isOnQuest = false;

	/* A quest that the knight is currently having */
	Quest quest;

	private volatile Agenda agendaNew;
	private volatile Agenda agendaComplete;
	private volatile Hall greatHall;

	Knight(int id, Agenda agendaNew, Agenda agendaComplete, Hall greatHall) {
		super();
		this.id = id;
		this.agendaNew = agendaNew;
		this.agendaComplete = agendaComplete;
		this.greatHall = greatHall;
	}

	/* Knight will follow the sequence of actions to execute the tasks and repeat */
	@Override
	public void run() {
		// Make knights keep working
		while (!isInterrupted()) {
			try {

				greatHall.enterGreatHall(this);
				// Wait for knight to spend mingling time before table a seat
				sleep(Params.getMinglingTime());

				greatHall.sitTable(this);
				greatHall.releaseQuest(this);
				greatHall.acquireQuest(this);
				greatHall.standUp(this);

				// Wait for knight to spend mingling time before leave the hall
				sleep(Params.getMinglingTime());
				greatHall.leaveGreatHall(this);

				// Wait for knight to complete quest
				sleep(Params.getQuestingTime());
				greatHall.completeQuest(this);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public int getKnightId() {
		return this.id;
	}

	public synchronized boolean isSit() {
		return isSit;
	}

	public synchronized void setSit(boolean isSit) {
		this.isSit = isSit;
	}

	public synchronized boolean isAvailable() {
		return isAvailable;
	}

	public synchronized void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public synchronized boolean isInHall() {
		return isInHall;
	}

	public synchronized void setInHall(boolean isInHall) {
		this.isInHall = isInHall;
	}

	public synchronized boolean isOnQuest() {
		return isOnQuest;
	}

	public synchronized void setOnQuest(boolean isOnQuest) {
		this.isOnQuest = isOnQuest;
	}

}
