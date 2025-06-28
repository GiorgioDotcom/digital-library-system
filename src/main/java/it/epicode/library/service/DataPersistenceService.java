package it.epicode.library.service;

import it.epicode.library.model.media.Media;
import it.epicode.library.model.structure.Library;
import it.epicode.library.model.exceptions.DataPersistenceException;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

public class DataPersistenceService {
    private static final Logger logger = LoggingService.getInstance().getLogger(DataPersistenceService.class);

    private final Path dataDirectory;
    private final Path backupDirectory;
    private static final String LIBRARY_FILE = "library.dat";
    private static final String CATALOG_CSV = "catalog.csv";
    private static final String CONFIG_FILE = "library.properties";

    public DataPersistenceService(String dataPath) {
        this.dataDirectory = Paths.get(dataPath);
        this.backupDirectory = dataDirectory.resolve("backups");

        try {
            Files.createDirectories(dataDirectory);
            Files.createDirectories(backupDirectory);
        } catch (IOException e) {
            throw new DataPersistenceException("initialize", dataPath, e);
        }
    }

    /**
     * Saves library data using binary serialization.
     */
    public void saveLibrary(Library library) {
        ExceptionShieldingService.executeVoidWithShielding(() -> {
            Path libraryPath = dataDirectory.resolve(LIBRARY_FILE);
            Path tempPath = dataDirectory.resolve(LIBRARY_FILE + ".tmp");

            // Write to temporary file first
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new BufferedOutputStream(Files.newOutputStream(tempPath)))) {

                oos.writeObject(library);
                oos.flush();

                logger.log(Level.INFO, "Library data serialized to temporary file");

                // Atomic move to final location
                Files.move(tempPath, libraryPath, StandardCopyOption.REPLACE_EXISTING);

                logger.log(Level.INFO, "Library saved successfully to {0}", libraryPath);

            } catch (IOException e) {
                // Clean up temp file if it exists
                try {
                    Files.deleteIfExists(tempPath);
                } catch (IOException cleanupException) {
                    logger.log(Level.WARNING, "Failed to clean up temp file", cleanupException);
                }
                throw new RuntimeException(e); // Will be caught by exception shielding
            }
        }, "saveLibrary");
    }

    /**
     * Loads library data from binary file.
     */
    public Optional<Library> loadLibrary() {
        return ExceptionShieldingService.executeWithShielding(() -> {
            Path libraryPath = dataDirectory.resolve(LIBRARY_FILE);

            if (!Files.exists(libraryPath)) {
                logger.log(Level.INFO, "No existing library file found");
                return null;
            }

            try (ObjectInputStream ois = new ObjectInputStream(
                    new BufferedInputStream(Files.newInputStream(libraryPath)))) {

                Library library = (Library) ois.readObject();
                logger.log(Level.INFO, "Library loaded successfully from {0}", libraryPath);
                return library;

            } catch (ClassNotFoundException e) {
                throw new RuntimeException(new IOException("Invalid library file format", e));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, "loadLibrary");
    }

    /**
     * Exports catalog to CSV format using NIO.2.
     */
    public void exportCatalogToCsv(List<Media> mediaList) {
        ExceptionShieldingService.executeVoidWithShielding(() -> {
            Path csvPath = dataDirectory.resolve(CATALOG_CSV);

            try (BufferedWriter writer = Files.newBufferedWriter(csvPath, StandardCharsets.UTF_8)) {
                // Write header
                writer.write("ID,Type,Title,Author,Identifier,Available,Location,AcquisitionDate");
                writer.newLine();

                // Write data
                for (Media media : mediaList) {
                    writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s",
                            escapeCsvField(media.getId()),
                            escapeCsvField(media.getMediaType()),
                            escapeCsvField(media.getTitle()),
                            escapeCsvField(media.getMainAuthor()),
                            escapeCsvField(media.getIdentifier()),
                            media.isAvailable(),
                            escapeCsvField(media.getLocation()),
                            media.getAcquisitionDate()
                    ));
                    writer.newLine();
                }

                logger.log(Level.INFO, "Exported {0} media items to CSV: {1}",
                        new Object[]{mediaList.size(), csvPath});
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, "exportCatalogToCsv");
    }

    /**
     * Imports catalog from CSV file.
     */
    /**
     * Imports catalog from CSV file.
     */
    public List<Map<String, String>> importCatalogFromCsv() {
        Path csvPath = dataDirectory.resolve(CATALOG_CSV);

        if (!Files.exists(csvPath)) {
            logger.log(Level.INFO, "No CSV file found for import");
            return Collections.emptyList();
        }

        List<Map<String, String>> records = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return Collections.emptyList();
            }

            String[] headers = headerLine.split(",");
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] values = line.split(",");

                if (values.length == headers.length) {
                    Map<String, String> record = new HashMap<>();
                    for (int i = 0; i < headers.length; i++) {
                        record.put(headers[i].trim(), values[i].trim());
                    }
                    records.add(record);
                } else {
                    logger.log(Level.WARNING, "Skipping malformed line {0}: {1}",
                            new Object[]{lineNumber, line});
                }
            }

            logger.log(Level.INFO, "Imported {0} records from CSV", records.size());
            return records;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error importing CSV", e);
            throw new DataPersistenceException("import", csvPath.toString(), e);
        }
    }

    /**
     * Saves configuration properties.
     */
    public void saveConfiguration(Properties config) {
        ExceptionShieldingService.executeVoidWithShielding(() -> {
            Path configPath = dataDirectory.resolve(CONFIG_FILE);

            try (BufferedWriter writer = Files.newBufferedWriter(configPath, StandardCharsets.UTF_8)) {
                config.store(writer, "Library Configuration - " + LocalDateTime.now());
                logger.log(Level.INFO, "Configuration saved to {0}", configPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, "saveConfiguration");
    }

    /**
     * Loads configuration properties.
     */
    public Properties loadConfiguration() {
        return ExceptionShieldingService.executeWithShielding(() -> {
            Path configPath = dataDirectory.resolve(CONFIG_FILE);
            Properties config = new Properties();

            if (Files.exists(configPath)) {
                try (BufferedReader reader = Files.newBufferedReader(configPath, StandardCharsets.UTF_8)) {
                    config.load(reader);
                    logger.log(Level.INFO, "Configuration loaded from {0}", configPath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                // Create default configuration
                config.setProperty("library.name", "Default Library");
                config.setProperty("library.maxCapacity", "10000");
                config.setProperty("loan.defaultDays", "14");
                config.setProperty("loan.maxRenewals", "2");

                saveConfiguration(config);
                logger.log(Level.INFO, "Created default configuration");
            }

            return config;
        }, "loadConfiguration").orElse(new Properties());
    }

    /**
     * Creates a backup of the current library data.
     */
    public void createBackup() {
        ExceptionShieldingService.executeVoidWithShielding(() -> {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            Path backupFile = backupDirectory.resolve("library_backup_" + timestamp + ".dat");
            Path sourceFile = dataDirectory.resolve(LIBRARY_FILE);

            if (Files.exists(sourceFile)) {
                try {
                    Files.copy(sourceFile, backupFile, StandardCopyOption.REPLACE_EXISTING);
                    logger.log(Level.INFO, "Backup created: {0}", backupFile);

                    // Clean old backups (keep last 10)
                    cleanOldBackups(10);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "createBackup");
    }

    /**
     * Lists available backups.
     */
    /**
     * Lists available backups.
     */
    /**
     * Lists available backups.
     */
    public List<Path> listBackups() {
        try {
            if (!Files.exists(backupDirectory)) {
                return Collections.emptyList();
            }

            List<Path> backups = new ArrayList<>();
            try (var stream = Files.list(backupDirectory)) {
                stream.filter(path -> {
                            String fileName = path.getFileName().toString();
                            return fileName.startsWith("library_backup_") && fileName.endsWith(".dat");
                        })
                        .sorted((p1, p2) -> p2.getFileName().toString().compareTo(p1.getFileName().toString()))
                        .forEach(backups::add);
            }

            return backups;
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error listing backups", e);
            return Collections.emptyList();
        }
    }

    /**
     * Restores library from backup.
     */
    public Optional<Library> restoreFromBackup(Path backupFile) {
        return ExceptionShieldingService.executeWithShielding(() -> {
            if (!Files.exists(backupFile)) {
                throw new RuntimeException(new FileNotFoundException("Backup file not found: " + backupFile));
            }

            try (ObjectInputStream ois = new ObjectInputStream(
                    new BufferedInputStream(Files.newInputStream(backupFile)))) {

                Library library = (Library) ois.readObject();
                logger.log(Level.INFO, "Library restored from backup: {0}", backupFile);
                return library;

            } catch (ClassNotFoundException e) {
                throw new RuntimeException(new IOException("Invalid backup file format", e));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, "restoreFromBackup");
    }

    /**
     * Writes log files for audit trail.
     */
    public void writeAuditLog(String operation, String details) {
        ExceptionShieldingService.executeVoidWithShielding(() -> {
            Path logFile = dataDirectory.resolve("audit.log");
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            String logEntry = String.format("[%s] %s: %s%n", timestamp, operation, details);

            try (BufferedWriter writer = Files.newBufferedWriter(logFile, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                writer.write(logEntry);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, "writeAuditLog");
    }

    // Helper methods
    private String escapeCsvField(String field) {
        if (field == null) return "";
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }

    private void cleanOldBackups(int keepCount) {
        try {
            List<Path> backups = listBackups();
            if (backups.size() > keepCount) {
                for (int i = keepCount; i < backups.size(); i++) {
                    Files.deleteIfExists(backups.get(i));
                    logger.log(Level.INFO, "Deleted old backup: {0}", backups.get(i));
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to clean old backups", e);
        }
    }

    /**
     * Gets directory information.
     */
    public Map<String, Object> getDirectoryInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("dataDirectory", dataDirectory.toString());
        info.put("backupDirectory", backupDirectory.toString());
        info.put("hasLibraryFile", Files.exists(dataDirectory.resolve(LIBRARY_FILE)));
        info.put("hasCsvFile", Files.exists(dataDirectory.resolve(CATALOG_CSV)));
        info.put("hasConfigFile", Files.exists(dataDirectory.resolve(CONFIG_FILE)));
        info.put("backupCount", listBackups().size());

        return info;
    }
}