package exception;

public class DuplicateEntityException extends RuntimeException {
	public DuplicateEntityException(Object entity) {
		super("Object name: " + entity.getClass().getSimpleName() + " with id " + getIdValue(entity)
				+ " already exists");
	}

	// Helper method to try to extract the 'id' field via reflection (optional)
	private static Object getIdValue(Object entity) {
		try {
			var field = entity.getClass().getDeclaredField("id");
			field.setAccessible(true);
			return field.get(entity);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			return "unknown";
		}
	}
}
