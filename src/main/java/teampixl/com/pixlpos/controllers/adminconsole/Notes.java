package teampixl.com.pixlpos.controllers.adminconsole;

import teampixl.com.pixlpos.models.tools.DataManager;
import teampixl.com.pixlpos.models.tools.MetadataWrapper;
import teampixl.com.pixlpos.database.api.UserStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * NotesApp class is a construct for creating NotesApp object. Extends DataManager.
 * <p>
 * Metadata:
 * - note_id: UUID
 * - user_id: UUID
 * - timestamp: timestamp
 * <p>
 * Data:
 * - note_title: Title
 * - note_content: Content
 * @see DataManager
 * @see MetadataWrapper
 */
public class Notes extends DataManager {
    public Notes(String NoteTitle, String NoteContent) {
        super(initializeMetadata());

        this.data.put("note_title", NoteTitle);
        this.data.put("note_content", NoteContent);
    }

    public Notes(String NoteContent) {
        super(initializeMetadata());

        this.data.put("note_title", "Untitled");
        this.data.put("note_content", NoteContent);
    }

    private static MetadataWrapper initializeMetadata() {
        Map<String, Object> metadataMap = new HashMap<>();
        metadataMap.put("note_id", UUID.randomUUID());
        metadataMap.put("user_id", UserStack.getInstance().getCurrentUserId());
        metadataMap.put("timestamp", System.currentTimeMillis());
        return new MetadataWrapper(metadataMap);
    }

    public static class NotesMethods {

    }
}
