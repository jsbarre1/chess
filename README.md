# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```

link to server design edit mode:

https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIei0azF5vDgHYsgwr5kvDrco67F8H5LCBALnAWspqig5QIAePKwvuh6ouisTYgmhgumGbpkhSBq0uWo4mkS4YWhy3K8gagrCjAr5iK60pJpesHlEa2iOs6rFOvKJRIAAZpYhEcfI776K8SyYXK2EErhLIerqmSxv604hkx5qRhy0YwCp8YyZ2-5pihPLZrmmBGZ2P5XAMxGjAuk59NOs7NuOrZ9N+V4gqc2Q9jA-aDr0tkjvZbnVlOQYufOYVtsuq7eH4gReCg6B7gevjMMe6SZJgvkXkU1DXtIACiu7FfUxXNC0D6qE+3TOY26Dtr+ZxAiWDVzhZUEdjBvFwTACH2BlyGIWgaEYtJsEavJ2qpCgIANipAaRY1aCkUyboUaU5LMHpnEaRGvXgjAamcVh01kXhHAoNwylBstcbaOtZoRoUlrSDdFKGHt8hcYmybdcZ6W+mZCB5oDVleVcnmFd5hR5WAfYDkOS6cPF66BJCtq7tCMAAOKjqyWWnrl57ML1154+VVX2KO9UrZ1P5skZ5QdU1llHfKMA7QToyqA9M6rZNfUXRtCkgHNC1LWza2hpdzFvRyO26UGDoHZDbGnb9504fLpI84TsLPeRWnbRSJ3KtzhNy2Lmmc-1tN8xUjSmDrANtWmBt86DWBdR7GslFcUyO2o4yVP0IcAJLSGHACMvYAMwACxPCemQGhWEwzN0fQ6AgoANhnwFZ08IcAHKjlnewwI0MPHCxXY5Ij-nI70weE2HFQR6O0dx4nKdTGn+p2fcfTZ0kuf5wtRf2SX7ejBXoxVzXqMrp4CUbtgPhQNg3DwEphi84YxM5QjbLWZUtQNDTdPBAz6BDuXo51z1f6A6z99oLM3cL8X7mQf7e25RPR6iPgLKKswn6jGFtxGQM13QS3mote6MtjabVNsrH6wAbYvQDsdLW2CdZyT1ggg+YCoEoDQQrdkpRgA2ituKdWDcsJkh7tIV2BkG4sxgCAzIR8famA5hTNMPR54oF7uUeOycYAvzho3PyAUhxiIkTAKRScZFmDRuvDGARLA3QQskGAAApCAPJ8ajkCHnAupMm7nyhpfSkd4Wgh3pnWVaQ5d7AD0VAOAEAEJQEgWw2RzN34nU-t-Tx3jfH+MCaMaOADUx2LYjAAAVqYtAYCZbvnzlEvx0BYniOkDAxMxDbYIMlsgv04ChY4JNorM2u1Vb7XgXgrmBCOFTV1mU2hNoqg5MoPWchbDsleMoNE-JYTHryBcAxbEtT0H1LobaSJHZIAMLEH9bC7tUzlDSTyfhahzJ+0Scw+xPRgkNwRkjQK5zNFrzXIlAIXgvFdi9LAYA2Bd6EHiIkFI2Uzy2OEeUCoJUyoVSqsYZqISPalGOdBHix1rq3RQNIAAQkbTZosXqlCRV9A0qhGQYqYfU3FmR6IdFUPUXQ3BMVdOxaSlAR8CXaCJS002DL1msnkHnMQRC4EkJxZ9O6UzgCspIVtDl04YDcppR0kWXDQkMrRQIuFPUgWlguT5Mm1yUZ3MwEAA
link to server design presentation mode:

https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIei0azF5vDgHYsgwr5kvDrco67F8H5LCBALnAWspqig5QIAePKwvuh6ouisTYgmhgumGbpkhSBq0uWo4mkS4YWhy3K8gagrCjAr5iK60pJpesHlEa2iOs6rFOvKJRIAAZpYhEcfI776K8SyYXK2EErhLIerqmSxv604hkx5qRhy0YwCp8YyZ2-5pihPLZrmmBGZ2P5XAMxGjAuk59NOs7NuOrZ9N+V4gqc2Q9jA-aDr0tkjvZbnVlOQYufOYVtsuq7eH4gReCg6B7gevjMMe6SZJgvkXkU1DXtIACiu7FfUxXNC0D6qE+3TOY26Dtr+ZxAiWDVzhZUEdjBvFwTACH2BlyGIWgaEYtJsEavJ2qpCgIANipAaRY1aCkUyboUaU5LMHpnEaRGvXgjAamcVh01kXhHAoNwylBstcbaOtZoRoUlrSDdFKGHt8hcYmybdcZ6W+mZCB5oDVleVcnmFd5hR5WAfYDkOS6cPF66BJCtq7tCMAAOKjqyWWnrl57ML1154+VVX2KO9UrZ1P5skZ5QdU1llHfKMA7QToyqA9M6rZNfUXRtCkgHNC1LWza2hpdzFvRyO26UGDoHZDbGnb9504fLpI84TsLPeRWnbRSJ3KtzhNy2Lmmc-1tN8xUjSmDrANtWmBt86DWBdR7GslFcUyO2o4yVP0IcAJLSGHACMvYAMwACxPCemQGhWEwzN0fQ6AgoANhnwFZ08IcAHKjlnewwI0MPHCxXY5Ij-nI70weE2HFQR6O0dx4nKdTGn+p2fcfTZ0kuf5wtRf2SX7ejBXoxVzXqMrp4CUbtgPhQNg3DwEphi84YxM5QjbLWZUtQNDTdPBAz6BDuXo51z1f6A6z99oLM3cL8X7mQf7e25RPR6iPgLKKswn6jGFtxGQM13QS3mote6MtjabVNsrH6wAbYvQDsdLW2CdZyT1ggg+YCoEoDQQrdkpRgA2ituKdWDcsJkh7tIV2BkG4sxgCAzIR8famA5hTNMPR54oF7uUeOycYAvzho3PyAUhxiIkTAKRScZFmDRuvDGARLA3QQskGAAApCAPJ8ajkCHnAupMm7nyhpfSkd4Wgh3pnWVaQ5d7AD0VAOAEAEJQEgWw2RzN34nU-t-Tx3jfH+MCaMaOADUx2LYjAAAVqYtAYCZbvnzlEvx0BYniOkDAxMxDbYIMlsgv04ChY4JNorM2u1Vb7XgXgrmBCOFTV1mU2hNoqg5MoPWchbDsleMoNE-JYTHryBcAxbEtT0H1LobaSJHZIAMLEH9bC7tUzlDSTyfhahzJ+0Scw+xPRgkNwRkjQK5zNFrzXIlAIXgvFdi9LAYA2Bd6EHiIkFI2Uzy2OEeUCoJUyoVSqsYZqISPalGOdBHix1rq3RQNIAAQkbTZosXqlCRV9A0qhGQYqYfU3FmR6IdFUPUXQ3BMVdOxaSlAR8CXaCJS002DL1msnkHnMQRC4EkJxZ9O6UzgCspIVtDl04YDcppR0kWXDQkMrRQIuFPUgWlguT5Mm1yUZ3MwEAA