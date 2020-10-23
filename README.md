# Assignment Details
Please execute:
`mvn clean install` to build the project and execute the tests (unit tests) for the project.
Comments within code are also added. Following additions and changes are done to the assignment.
- Added lombok to remove boiler plate code from entity classes.
- Added Proper Error Messages and Threw Exceptions where needed.
- Added Validations to method arguments.
- Added Unit Tests for addTracks(String, List, int) service method, that verify all business aspects of that method, and also verify the interactions.
- Refactored addTracks() to: 
    - Add Validations
    - Remove redundancy
    - Improve readability
    - Improve time complexity
- Implemented RemoveTracks() with Validations, in Readable, Efficient and time efficient manner.
- Added Unit Tests for removeTracks(String) service method, that verify all business aspects of that method, and also verify the interactions.

# Tidal recruiting refactoring test

###### Please do not share this test or your answers on github or any other place where it can be found by a third party. 

##Intro
The code in this project is a isolated and very simplified version of our add-track-to-playlist business code.

##Assignments
1. Add necessary unit tests to the existing code to both see that the code works as expected, and to avoid breaking the code when refactoring
2. Refactor the existing code until you are satisfied with it
3. Implement the `removeTracks` method with appropriate unit tests

##Feel you are done or have questions?
When you have managed to make yourself believe that you are done, please send us a zip file with the code. If you have any question about the test do not hesitate to contact us.

Enjoy!
