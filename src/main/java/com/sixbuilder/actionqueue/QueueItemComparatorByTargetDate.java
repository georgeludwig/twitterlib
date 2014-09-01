package com.sixbuilder.actionqueue;

import java.util.Comparator;

public class QueueItemComparatorByTargetDate implements Comparator<QueueItem> {

	public int compare(QueueItem arg0, QueueItem arg1) {
		if(arg0.getTargetDate()<arg1.getTargetDate())
			return -1;
		if(arg0.getTargetDate()>arg1.getTargetDate())
			return 1;
		return 0;
	}
	
}
