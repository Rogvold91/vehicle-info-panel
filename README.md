# vehicle-info-panel

The simplest Vehicle info panel.


1. Service SpeedService.java

It provides updates about CarData for all listeners.
It register/ callbacks from clients.
It starts as soon as Boot complete intent is received.


2. Data is generated by special generators.
You can easy define own generator and implement related interface. 
If you want to extends used data information you should extends same class only.

3. Page fragments are used to swipe by two fingers.
Swipe by alone finger is not allowed.

4. Drawed view can be easy customized via resources.
5. Main activity is client working with SpeedService.