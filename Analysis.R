##############################################################
##########          Project                        ###########
# Database to effeciently store and retrieve timeseries data #
##########          Submitted by Sarvani Vadali    ###########
##############################################################

rm(list = ls())

library(mongolite)

# Aim of the script:
#   Perform analysis on Timeseries data stored in MongoDB database.
#   MondoDB is structured using wide table scheme.
#   Wide table schema allows to get data points with simple queries
#   Following script implements essential functions needed to understand
#   stored (simulated temperature) data. There are a total of 1 million
#   data points in sample data stored in MongoDB database.

#####   Queries to get basic structure of Timeseries data   #####

# Get number of distinct Timeseries objects in database
numberOfTimeseries <- function () {
  mongoData <- mongo(collection = metadata, db = db)
  return (mongoData$count())
}

# Total datapoints in database
totalDataPoints <- function() {
  mongoData <- mongo(collection = points, db = db)
  df <- mongoData$aggregate('[{"$group":{"_id":"$ID", "count": {"$sum":"$Count"}}}]')
  return (sum(df$count))
}

# Gets ID of all Timeseries object stored in database
getIDs <- function() {
  mongoData <- mongo(collection = metadata, db = db)
  
  # We want to get IDs only.
  fields <- '{"ID": 1, "_id": 0}'
  df <- mongoData$find(fields = fields)
  
  return (as.vector(df$ID))
}

# Get number of data points in a given Timeseries object
numberOfDataPoints <- function(ID) {
  mongoData <- mongo(collection = points, db = db)
  df <- mongoData$aggregate('[{"$group":{"_id":"$ID", "count": {"$sum":"$Count"}}}]')
  return (df[ID, 'count'])
}

# Get average of data points for a given Timeseries
averageOfDataPonts <- function(ID) {
  mongoData <- mongo(collection = points, db = db)
  df <- mongoData$aggregate('[{"$group":{"_id":"$ID", "count": {"$sum":"$Count"}, "total": {"$sum":"$Total"}}}]')
  return (df[ID, 'total'] / df[ID, 'count'])
}

#####   Functionality to plot Timeseries data   #####

# Helper function to get all the data points as a data.frame
getDataPoints <- function(ID) {
  mongoData <- mongo(collection = points, db = db)
  
  # We want to get only data points. Restrict data points.
  fields <- '{"Count": 0, "Total": 0, "_id": 0, "ID": 0, "Time": 0}'
  
  # Query only for given timeseries.
  query <- sprintf('{"ID":%d}', ID)
  
  df <- mongoData$find(query = query, fields = fields)
  return (df)
}

# Plots all points in a given Timeseries.
plotATimeseries <- function(ID) {
  df <- getDataPoints(ID)
  
  # Now, plot given time series.
  plot(as.vector(as.matrix(df)),  type = "s")
}

# Helper to get count and totals for given Timeseries data.
getDataPointSummary <- function(ID) {
  mongoData <- mongo(collection = points, db = db)
  
  # We want to get only data points. Restrict data points.
  fields <- '{"Count": 1, "Total": 1, "_id": 0}'
  
  # Query only for given timeseries.
  query <- sprintf('{"ID":%d}', ID)
  
  df <- mongoData$find(query = query, fields = fields)
  
  df$Average = (df$Total/df$Count)
  
  return (df)
}

# Plot minute by minute averages for given Timeseries
plotMinutelyAverages <- function(ID) {
  df <- getDataPointSummary(ID)
  
  # Now, plot averages only.
  plot(as.vector(df$Average), type="o", col="red")
}

#####   Advanced queries related to single Timeseries   #####

# Gets Times of given Timeseries data
getStartTimes <- function(ID) {
  mongoData <- mongo(collection = points, db = db)
  
  # We want to get only data points. Restrict data points.
  fields <- '{"Time": 1, "_id": 0}'
  
  # Query only for given timeseries.
  query <- sprintf('{"ID":%d}', ID)
  
  df <- mongoData$find(query = query, fields = fields)
  df$Time <- as.POSIXct(df$Time, origin = "1970-01-01")
  
  return (df)
}

# Get duration of Timeseries data
getDurationOfTimeseries <- function(ID) {
  df <- getStartTimes(ID)
  
  return (df[nrow(df), ] - df[1, ])
}

# Below function down samples given timeseries
downSampleTimeseries <- function(ID, numOfPoints = 100) {
  totalPoints <- numberOfDataPoints(ID)
  df <- getDataPoints(ID)
  allPoints <- as.vector(as.matrix(df))
  
  if (totalPoints > numOfPoints) {
    pointsToPlot <- vector(mode = "numeric", length = numOfPoints)
    
    # We need to down sample it.
    k <- totalPoints / numOfPoints # Simply pick every k points
    
    i <- 1
    index <- 1
    
    while (i <= numOfPoints) {
      pointsToPlot[i] <- allPoints[index]
      i <- i + 1
      index <- index + k
    }
    
    return (pointsToPlot)
  }
  
  return (allPoints)
}

# Function to plot down sampled values
plotDownSampledValues <- function(ID) {
  vals <- downSampleTimeseries(ID)
  
  plot(vals, type="o", col="blue")
}

###################################
##########  MAIN SCRIPT  ##########
###################################

# Globals
db <- "Timeseries"
metadata <- "Metadata"
points <- "Points"
ID <- 5

print(sprintf("Total number of Timeseries object in database %d", numberOfTimeseries()))

print(sprintf("Total number of data points in database %d", totalDataPoints()))

print(sprintf("Number of data points in Timeseries (%d) = %d", ID, numberOfDataPoints(ID)))

print(sprintf("Average temperature of Timeseries (%d) = %s", ID, as.character(averageOfDataPonts(ID))))

# Sample plots
plotMinutelyAverages(ID)

plotATimeseries(ID)

plotDownSampledValues(ID)