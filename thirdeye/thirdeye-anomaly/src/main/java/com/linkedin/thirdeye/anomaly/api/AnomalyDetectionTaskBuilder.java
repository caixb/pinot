package com.linkedin.thirdeye.anomaly.api;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkedin.thirdeye.anomaly.api.external.AnomalyDetectionFunction;
import com.linkedin.thirdeye.anomaly.database.FunctionTable;
import com.linkedin.thirdeye.anomaly.database.FunctionTableRow;
import com.linkedin.thirdeye.anomaly.handler.AnomalyResultHandlerDatabase;
import com.linkedin.thirdeye.anomaly.util.ThirdEyeMultiClient;
import com.linkedin.thirdeye.api.StarTreeConfig;
import com.linkedin.thirdeye.api.TimeRange;
import com.linkedin.thirdeye.client.ThirdEyeClient;

/**
 * Loads rules from the function table in the anomaly database.
 */
public class AnomalyDetectionTaskBuilder {

  private static final Logger LOGGER = LoggerFactory.getLogger(AnomalyDetectionTaskBuilder.class);

  private final List<AnomalyDetectionDriverConfig> collectionDrivers;
  private final ThirdEyeMultiClient thirdEyeMultiClient;
  private final AnomalyDatabaseConfig dbConfig;

  public AnomalyDetectionTaskBuilder(List<AnomalyDetectionDriverConfig> collectionDrivers,
      AnomalyDatabaseConfig dbConfig, ThirdEyeMultiClient thirdEyeMultiClient) {
    super();
    this.collectionDrivers = collectionDrivers;
    this.dbConfig = dbConfig;
    this.thirdEyeMultiClient = thirdEyeMultiClient;
  }

  /**
   * @param collectionsConfig
   * @param dbConfig
   * @param timeRange
   * @return
   *  A list of AnomalyDetectionTasks to execute
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws IOException
   * @throws SQLException
   */
  public List<AnomalyDetectionTask> buildTasks(TimeRange timeRange, AnomalyDetectionFunctionFactory functionFactory)
      throws InstantiationException, IllegalAccessException, IOException, SQLException {

    List<AnomalyDetectionTask> tasks = new LinkedList<AnomalyDetectionTask>();

    List<? extends FunctionTableRow> rows = FunctionTable.selectRows(dbConfig, functionFactory.getFunctionRowClass());

    for (FunctionTableRow functionTableRow : rows) {
      try {
        String collectionName = functionTableRow.getCollectionName();

        // find the collection
        AnomalyDetectionDriverConfig collectionDriverConfig = AnomalyDetectionDriverConfig.find(collectionDrivers,
            collectionName);

        // load star tree
        StarTreeConfig starTreeConfig = thirdEyeMultiClient.getStarTreeConfig(collectionName);

        // get the shared client
        ThirdEyeClient sharedThirdEyeClient = thirdEyeMultiClient.getClient(collectionName);

        // load the function
        AnomalyDetectionFunction function = functionFactory.getFunction(starTreeConfig, dbConfig, functionTableRow);

        // create task info
        AnomalyDetectionTaskInfo taskInfo = new AnomalyDetectionTaskInfo(functionTableRow.getFunctionName(),
            functionTableRow.getFunctionId(), functionTableRow.getFunctionDescription(), timeRange);

        // make a handler
        AnomalyResultHandler resultHandler = new AnomalyResultHandlerDatabase(dbConfig);
        resultHandler.init(starTreeConfig, new HandlerProperties());

        // make the function history interface
        AnomalyDetectionFunctionHistory functionHistory = new AnomalyDetectionFunctionHistory(starTreeConfig, dbConfig,
            functionTableRow.getFunctionId());

        // make the task
        AnomalyDetectionTask task = new AnomalyDetectionTask(starTreeConfig, collectionDriverConfig, taskInfo,
            function, resultHandler, functionHistory, sharedThirdEyeClient);

        tasks.add(task);
      } catch (Exception e) {
        LOGGER.error("could not create function for function_id={}", functionTableRow.getFunctionId(), e);
      }
    }

    return tasks;
  }
}
