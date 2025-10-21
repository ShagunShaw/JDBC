# This is the dockerfile for the Hotel Reservation System Application

FROM openjdk:jdk-25


WORKDIR /app

COPY dotenv-java-3.0.0.jar dotenv-java-3.0.0.jar
COPY mysql-connector-j-9.4.0.jar mysql-connector-j-9.4.0.jar
COPY HotelReservationSystem.java HotelReservationSystem.java