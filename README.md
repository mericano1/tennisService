# Tennis service

## Setup

This project uses sbt. You can use intellij -> Open and point to the folder to import the project.

## Run

To run the service you can use

    sbt run
    
To install sbt refer to this guide [http://www.scala-sbt.org/0.13/tutorial/Setup.html]


To run the tests you can use

    sbt clean test


## Trying the service


- Create a new game

    curl -XPOST http://127.0.0.1:8080/game -d '{ \
        "playerOne" : {"name" : "Elvis"}, \
        "playerTwo" : {"name" : "John"} \ 
        }' \ 
    --header "Content-type:application/json"


You should get back an identifier
  
    {"id":"3d2b4259-54a4-4ab0-80e5-52f4f62842a4"}
   
   

- Get match details back

    curl http://127.0.0.1:8080/game/3d2b4259-54a4-4ab0-80e5-52f4f62842a4
    
Example response:
    
    {
      "playerOne": {
        "name": "Elvis"
      },
      "playerTwo": {
        "name": "John"
      },
      "status": "Ongoing",
      "durationInSec": 45,
      "score": {
        "sets": [
          {
            "games": [
              {
                "playerOne": {
                  "player": {
                    "name": "Elvis"
                  },
                  "points": 0,
                  "advantage": false
                },
                "playerTwo": {
                  "player": {
                    "name": "John"
                  },
                  "points": 0,
                  "advantage": false
                }
              }
            ]
          }
        ]
      }
    }
    
    
- Update the score:

    curl -XPUT http://127.0.0.1:8080/game/c15e7cae-ef7e-43de-861d-c05351705218 -d '{"scoring" : { "name" : "Elvis"}}'
    
    
You should get back a response like the following (current game):


    {
      "game": {
        "playerOne": {
          "player": {
            "name": "Elvis"
          },
          "points": 15,
          "advantage": false
        },
        "playerTwo": {
          "player": {
            "name": "John"
          },
          "points": 0,
          "advantage": false
        }gith
      }
    }
    
    
    
