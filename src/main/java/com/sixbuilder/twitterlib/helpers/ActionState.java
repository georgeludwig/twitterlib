package com.sixbuilder.twitterlib.helpers;

/**
 * Enum that represents the state of an {@link Action}.
 * @author Thiago H. de Paula Figueiredo (http://machina.com.br/thiago)
 */
public enum ActionState {
	
	QUEUED("Queued", "orange"),
	COMPLETED("Completed", "green"),
	ENABLED("Enabled", "black"),
	DISABLED("Disabled", "gray");
	
	private ActionState(String name, String color) {
		this.name = name;
		this.color = color;
	}
	
	final private String name;
	
	final private String color;

	/**
	 * Returns the value of the color field.
	 * @return a {@link String}.
	 */
	public String getColor() {
		return color;
	}
	
	public String getName() {
		return name;
	}
	
	public void set(Tweet tweet, Action action) {
		switch (this) {
			case QUEUED :
				action.setQueued(tweet, true);
				action.setCompleted(tweet, false);
				action.setEnabled(tweet, false);
				break;
			case COMPLETED :
				action.setQueued(tweet, false);
				action.setCompleted(tweet, true);
				action.setEnabled(tweet, false);
				break;
			case ENABLED :
				action.setQueued(tweet, false);
				action.setCompleted(tweet, false);
				action.setEnabled(tweet, true);
				break;
			case DISABLED :
				action.setQueued(tweet, false);
				action.setCompleted(tweet, false);
				action.setEnabled(tweet, false);
				break;
			default :
				break;
			
		}
	}

}
