package exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(Object entity) {
        super("Object name: " + entity.getClass().getSimpleName() 
              + " with id " + getIdValue(entity) + " not found");
    }

    // Helper method to extract an 'id' field if present
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
