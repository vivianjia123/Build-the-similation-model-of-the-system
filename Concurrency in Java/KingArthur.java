
/**
 * KingArthur is a thread that control the meeting process.
 */

/**
 * @author Ziqi Jia ziqij@student.unimleb.edu.au 693241
 *
 */

public class KingArthur extends Thread {

	private volatile Hall greatHall;

	KingArthur(Hall greatHall) {
		this.greatHall = greatHall;

	}

	/* King will start/end meeting based on knights' action */
	@Override
	public void run() {
		while (!interrupted()) {
			try {

				// Enter the Great Hall
				greatHall.kingEnter();
				// Start Meeting
				greatHall.StartMeeting();
				// End meeting
				greatHall.endMeeting();
				// Leave the Great Hall
				greatHall.kingLeave();
				// Wait for King to leave and re-enter the hall
				sleep(Params.getKingWaitingTime());

			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}

	}

}
