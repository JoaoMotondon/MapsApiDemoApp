# MapsApiDemoApp

Dealing with Google Maps API on an Android app is not so hard as it seems when we first look into it. We just need to understand some basic concepts and then put some pieces together.

You should first visit some official docs like [this](https://developers.google.com/maps/documentation/android-api/map) and [this](https://developers.google.com/maps/documentation/android-api/start). You could also check this [Udacity course](https://udacity.com/course/add-google-maps-to-your-android-app--ud876-4).

It is important to take sometime reading the docs, since there are some steps you must take prior to get your app up and running. One important step is to create an API key on the developer console in order to identify your project.

This project is an attempt to show you many things involving Google Maps working together in a single app. It uses Google Maps Android API v2. 

![Demo](https://user-images.githubusercontent.com/4574670/33396658-667a828a-d530-11e7-87d1-0314c8a639da.gif)

## With it you can:

  - Fly from one place to another (there are some fixed places already configured)
  - See lat/lng being updated at real time while you are flying on the map
  - Control zoom in and out
  - Add a marker to any place you want by choosing from 10 different marker colors
  - Add a circle up to to 5km of radious
  - Change map style [day and night styles]
  - Change map type [normal, Sattelite, hybrid]
  - Move a marker (long-click on it) and make maps to follow the marker
  
## It uses:
  - CameraPosition class to get locations (lat/lng)
  - GoogleMap::animateCamera() to get some animation when moving the camera between places
  - MarkerOptions and LatLng classes to define markers
  - GoogleMap::addCircle() method in order to add circles around the map
  - GoogleMap::setMapType() and GoogleMap::setMapStype() methods to change map type and style 
  - GoogleMap::OnMarkerDragListener and GoogleMap::animateCamera to move the map while moving a marker
  
When you see the code, you will realize how easy is to implement those features on your app. So, take it, read it and use it the way you want. Hope you enjoy it.

Thanks to Manoj Reddy [on this SO question](https://stackoverflow.com/questions/39191867/night-mode-for-google-maps) for the custom night map style.

If you want to know more about my projects, please, visit my [blog](http://androidahead.com).

# License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details
