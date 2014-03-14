package com.foursquare.android.masscheckin.classes;

import java.text.Collator;
import java.util.Locale;

public class Friends implements Comparable<Friends> {
	
	public String firstName;
	public String LastName;
	public String photoPrefix;
	public String photoSuffix;
	public boolean hasPhoto;
	public boolean isSelected;
	public String id;
	
	public String getSmallPhoto() {
		if (hasPhoto)
			return photoPrefix + "128x128" + photoSuffix;
		else
			return null;
	}

	@Override
	public int compareTo(Friends compareFriend) {
		Collator collator = Collator.getInstance(Locale.getDefault());

		return collator.compare(this.firstName, compareFriend.firstName);
	}
}
