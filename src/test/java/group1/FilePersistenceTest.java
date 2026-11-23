package group1;

import group1.service.FilePersistenceManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class FilePersistenceTest {
	@TempDir
	Path tempDir;

	@Test
	void persistenceRoundTripLoadsCustomers() throws Exception {
		Path file = tempDir.resolve("loan-management.dat");
		FilePersistenceManager manager = new FilePersistenceManager(file.toString());
		Customer customer = new Customer("C001", "John", "Banda", "NID001",
				"john@example.com", "Lilongwe", "Employed",
				"0999000000", 500000, "Active");

		manager.save(List.of(customer), List.of(), List.of(), List.of(), List.of());
		FilePersistenceManager.PersistentState state = manager.load();

		assertNotNull(state);
		assertEquals(1, state.getCustomers().size());
		assertEquals("C001", state.getCustomers().get(0).getCustomerId());
		assertFalse(java.nio.file.Files.exists(Path.of(file + ".tmp")));
	}
}
