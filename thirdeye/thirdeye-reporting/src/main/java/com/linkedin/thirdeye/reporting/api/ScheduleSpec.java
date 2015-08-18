package com.linkedin.thirdeye.reporting.api;

import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ScheduleSpec {

  private String cron;
  private int aggregationSize;
  private TimeUnit aggregationUnit;
  private int reportWindow;
  private int lagSize;
  private TimeUnit lagUnit;
  private String emailTo;
  private String emailFrom;
  private String nameTo;
  private String nameFrom;
  private boolean findAnomalies;

  public ScheduleSpec() {

  }

  public ScheduleSpec(String cron, int aggregationSize, TimeUnit aggregationUnit, int reportWindow, int lagSize,
      TimeUnit lagUnit, String emailTo, String emailFrom, boolean findAnomalies) {
    this.cron = cron;
    this.aggregationSize = aggregationSize;
    this.aggregationUnit = aggregationUnit;
    this.reportWindow = reportWindow;
    this.lagSize = lagSize;
    this.lagUnit = lagUnit;
    this.emailTo = emailTo;
    this.emailFrom = emailFrom;
    this.findAnomalies = findAnomalies;
  }

  public String getCron() {
    return cron;
  }

  public int getAggregationSize() {
    return aggregationSize;
  }

  public TimeUnit getAggregationUnit() {
    return aggregationUnit;
  }

  public int getReportWindow() {
    return reportWindow;
  }

  public int getLagSize() {
    return lagSize;
  }

  public TimeUnit getLagUnit() {
    return lagUnit;
  }

  public String getEmailTo() {
    return emailTo;
  }

  public void setEmailTo(String emailTo) {
    this.emailTo = emailTo;
  }

  public String getEmailFrom() {
    return emailFrom;
  }

  public void setEmailFrom(String emailFrom) {
    this.emailFrom = emailFrom;
  }

  public String getNameTo() {
    return nameTo;
  }

  public void setNameTo(String nameTo) {
    this.nameTo = nameTo;
  }

  public String getNameFrom() {
    return nameFrom;
  }

  public void setNameFrom(String nameFrom) {
    this.nameFrom = nameFrom;
  }

  public boolean isFindAnomalies() {
    return findAnomalies;
  }

  public void setFindAnomalies(boolean findAnomalies) {
    this.findAnomalies = findAnomalies;
  }

}
