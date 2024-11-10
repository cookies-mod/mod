package codes.cookies.mod.services;

/**
 * Result used to distinguish between three states of matching.
 */
public enum IsSameResult {
	YES,
	ALMOST,
	NO;

	public static IsSameResult wrapBoolean(boolean equals) {
		return equals ? YES : NO;
	}
}
