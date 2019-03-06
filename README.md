# BLE Application
This is an Android Application that connects to the closest Hue Bulb based on RSSI readings from TI Sensor Tags. 
Problem: User wants to control closest Hue bulb, but Hue bulb don't have proximity sensors
Solution: Each Hue bulb is associated with a specific beacon (in this case TI Sensor Tags). When the user clicks scan, the user gets a list of Hue bulbs and their associated TI Sensor Tag beacon RSSI values. These RSSI values can be used to indicate the proximity to a beacon. Therefore, the user can now control the bulb that is closest to him/her. 
