# Robomail-Revision

The Automail is an automated mail sorting and delivery system designed to operate in large buildings that have dedicated Mail rooms. It offers end to end receipt and delivery of mail items within the building, and can be tweaked to fit many different installation environments. The system consists of two key components:
- Delivery Robots which deliver mail items from the mail pool throughout the building. The robot can hold a single item in its "hands" and another in its tube, that is, the ‘backpack’ which is attached to each delivery robot. This tube can hold a single additional mail item, for delivery after the one held by the robot.
- A MailPool subsystem which holds mail items after their arrival at the building and which decides the order in which mail items should be delivered.

## Useful information 
- The mailroom is on the ground floor.
- All mail items are stamped with their time of arrival.
- Some mail items are priority mail items, and carry a priority from 100 high to 10 low.
- Prioritymailitemsarriveatthemailpoolandareregisteredoneatatime,sotimestampsareunique
for priority items. Normal (non-priority) mail items arrive at the mailpool in batches; all items in
a normal batch receive the same timestamp.
- The mailpool is responsible for giving mail items to the robots for delivery.
- A Delivery Robot carries at most two items. A Delivery Robot can be sent to deliver mail if its tube
is empty.
- All mail items have a weight from 200 grams up to a limit set as a system parameter. An item
can be carried by one to three robots (one only in the current system). The weight capacities are represented in the code by the constants (see Robot):
  - INDIVIDUAL_MAX_WEIGHT = 2000 – PAIR_MAX_WEIGHT = 2600
  - TRIPLE_MAX_WEIGHT = 3000
- The system generates a measure of the effectiveness of the system in delivering all mail items, taking into account time to deliver and priority. You do not need to be concerned about the detail of how this measure is calculated.
- A team of robots (whether made up of two or three robots) moves at one third the speed of an individual robot; where an individual robot moves one floor every time step (robot.step()), the team will wait for two time steps before moving on the third time step.


# About this project 
## Objective: Extended Design and Implementation 
As discussed above, and in order for the users of Automail to have confidence that changes have been made in a controlled manner, this program preserved the Automail simulation’s existing behaviour. The extended design and implementation accounts for the following:
- Preserve the behaviour of the system for configurations of where the maximum mail item weight is limited to that transportable by a single robot. (Preserve = identical output. We will use a file comparison tool to check this.)
- Add team behaviour to deal with heavier mail items.

## 1. Domain Class Diagram
A domain class diagram for the robot mail delivery domain, as reflected in the revised simulation. 
## 2. Static Design Model 
A static design model (design class diagram) of all changed and directly related components, complete with (for the changed components) visibility modifiers, attributes, associations, and public (at least) methods. This diagram must contain all relevant associations and dependencies (few if any tools do this automatically), and be well laid-out/readable; you should not expect to receive any marks for a diagram that is reverse engineered and submitted unchanged.  
## 3. Dynamic Design Model
A dynamic design model(design sequence diagram) illustrating how robots in your system transition from operating individually to working as a team and back to working individuals again. 

