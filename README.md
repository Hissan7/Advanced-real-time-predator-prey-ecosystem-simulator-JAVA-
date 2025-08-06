A real-time JavaFX predator-prey ecosystem simulator set in a 500x500 grid environment. The simulation models complex ecological interactions between three prey species, two predator species, and plant growth as a renewable resource. Features include disease spread within species, dynamic population tracking (including infected counts), and gender-based breeding systems influenced by genetic inheritance and mutation, enabling natural selection over time. The evolutionary algorithm adjusts key traits such as breeding age, lifespan, metabolism, and disease susceptibility. Plants regenerate in empty cells, supporting prey populations and driving predator-prey dynamics. Designed with a focus on real-time visualization, ecosystem balance, and emergent behavioral patterns.

This application is built to be run in the blueJ IDE
Here is a link to the official website (free download) : https://www.bluej.org

**How to run the application**

1. Run BlueJ
2. Go to the project tab on your screen (for MacOS it is in the top left)
3. Click on 'Open project'
4. Click on the package given
5. Once the package is opened in BlueJ you will see a diagram of many different classes. You can double-click on each class and see the source code, change it etc.
6. Press the 'compile' button in the top left of the BlueJ screen.
7. Right click on the 'SimulatorView' class.
8. Click 'Run JavaFX application'. This will load up the program and create an instance of the 'SimulatorView' object.
9. Right click on this instance and click 'void Simulate(int numStep)'
10. Enter the number of iterations you want the program to run
11. Watching the program run will show you how the ecosystem changes over time

There is a live count of the population of each animal and also the population of each animal that is infected with the disease. 
The legend shows which colour which animal corresponds to. 
