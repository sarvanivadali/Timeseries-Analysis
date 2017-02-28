# Timeseries-Analysis

## Title : Database to effeciently store and retrieve timeseries data

Following is my project submission to effeciently store timeseries data in (MongoDB) database and perform analysis using R.

## Components

- ```Analysis.R``` Performs analysis on Timeseries data stored in MongoDB database. MondoDB is structured using wide table scheme. Wide table schema allows to get data points with simple queries.

- ```Timeseries``` Implements (in Java) a Server application running on a well known (agreed) port and stores data points in MondoDB.

- ```Simulator``` Simulates (in Java) a Client that generates data points and sends them over to the Server.

