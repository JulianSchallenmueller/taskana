package pro.taskana;

import pro.taskana.configuration.TaskanaEngineConfiguration;

/**
 * The TaskanaEngine represents an overall set of all needed services.
 */
public interface TaskanaEngine {

    /**
     * The TaskService can be used for operations on all Tasks.
     * @return the TaskService
     */
    TaskService getTaskService();

    /**
     * The WorkbasketService can be used for operations on all Workbaskets.
     * @return the TaskService
     */
    WorkbasketService getWorkbasketService();

    /**
     * The ClassificationService can be used for operations on all Categories.
     * @return the TaskService
     */
    ClassificationService getClassificationService();

    /**
     * Can be used for each operation for summaries with short-infos about
     * one or more objects.
     * @return a SummaryService-BEAN
     */
    SummaryService getSummaryService();

    /**
     * The Taskana configuration.
     * @return the TaskanaConfiguration
     */
    TaskanaEngineConfiguration getConfiguration();

    /**
     * sets the connection management mode for taskana.
     *
     * @param mode. See ConnectionManagementMode
     */
    void setConnectionManagementMode(ConnectionManagementMode mode);

    /**
     * Set the connection to be used by taskana in mode CONNECTION_MANAGED_EXTERNALLY.
     * If this Api is called, taskana uses the connection passed by the client for all subsequent API calls
     * until the client resets this connection.
     * Control over commit and rollback of the connection is the responsibility of the client.
     * In order to close the connection, closeConnection() or setConnection(null) has to be called.
     *
     * @param connection - The java.sql.Connection that is controlled by the client
     */
    void setConnection(java.sql.Connection connection);

    /**
     *  Closes the client's connection, sets it to null and switches to mode PARTICIPATE.
     *  Only applicable in mode EXPLICIT. Has the same effect as setConnection(null).
     */
    void closeConnection();

    /**
     * Connection management mode.
     * Controls the connection handling of taskana
     *  PARTICIPATE       - taskana participates in global transaction. This is the default mode
     *  AUTOCOMMIT        - taskana commits each API call separately
     *  EXPLICIT          - commit processing is managed explicitly by the client
     */
    enum ConnectionManagementMode {
        PARTICIPATE,
        AUTOCOMMIT,
        EXPLICIT
    }

}
