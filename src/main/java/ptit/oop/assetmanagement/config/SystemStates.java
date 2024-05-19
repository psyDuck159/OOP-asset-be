package ptit.oop.assetmanagement.config;

import lombok.Getter;

public class SystemStates {

	@Getter
	public enum AssignmentStates {
		WAITING_FOR_ACCEPTANCE("Waiting for acceptance"),
		ACCEPTED("Accepted"),
		DECLINED("Declined"),
		RETURNING("Returning"),
		CLOSED("Closed");

		private final String state;

		private AssignmentStates(String state) {
			this.state = state;
		}
	}

	@Getter
	public enum AssetStates {
		AVAILABLE("Available"),
		NOT_AVAILABLE("Not available"),
		ASSIGNED("Assigned"),
		WAITING_FOR_RECYCLING("Waiting for recycling"),
		RECYCLED("Recycled");

		private final String state;

		private AssetStates(String state) {
			this.state = state;
		}
	}

	@Getter
	public enum ReturnStates {
		WAITING_FOR_RETURNING("Waiting for returning"),
		COMPLETED("Completed"),
		CANCELED("Canceled");

		private final String state;

		private ReturnStates(String state) {
			this.state = state;
		}
	}
}
