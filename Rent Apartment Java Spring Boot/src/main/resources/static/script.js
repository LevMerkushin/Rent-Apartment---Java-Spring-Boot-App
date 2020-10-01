//This is cities collection
var cities = [{
  city: "herzel st' 12",
  desc: "cool small place fo partners",
  lat: 32.08088,
  long: 34.78057
}];

//Create angular controller.
var app = angular.module('googleAapApp', []);
app.controller('googleAapCtrl', function($scope,$http) {
  $scope.show = true
  $scope.update = false
  $scope.formHeader = 'Login'
  $scope.start = 'login'
  $scope.house = {};
  $scope.actionToAdd = {};
  $scope.createBoard = false;
  $scope.apartments_boards = [];
  $scope.roleList = ["PLAYER", "MANAGER", "ADMIN"];
  $scope.nameList = ["House of land", "Apartment in building", "Basement"];
  $scope.boards = ["North", "Center", "South"];
  $scope.avatartList = ["Mr Bin", "Pikachu", "King","Strong woman"];
  $scope.board = {type:'BOARD',active : true,createdBy:""};

  $scope.startUser = {
    email: 'start@gmail.com',
    role: 'MANAGER',
    username:  'start',
    avatar: 'Pikachu',
    };


    

    $http.post('http://localhost:8095/acs/users', JSON.stringify($scope.startUser)).then(function (response) {
      $scope.startUser = response.data;
      $http.get('http://localhost:8095//acs/elements/'+$scope.startUser.email+'/search/ByType/'+$scope.board.type).then(function (response) {
      $scope.apartments_boards = response.data;
      if ($scope.apartments_boards.length == 0){
        if(  $scope.createBoard == false)
        {
          $scope.createBoard == true
        $scope.board.createdBy = $scope.startUser.email
        $scope.board.name = 'North'
        $http.post('http://localhost:8095//acs/elements/'+$scope.startUser.email, JSON.stringify($scope.board)).then(function (response) {
          $scope.Rboard = response.data;
          $scope.apartments_boards.push($scope.Rboard);
          $scope.board.name = 'Center'
          $http.post('http://localhost:8095//acs/elements/'+$scope.startUser.email, JSON.stringify($scope.board)).then(function (response) {
            $scope.Rboard = response.data;
            $scope.apartments_boards.push($scope.Rboard);
            $scope.board.name = 'South'          
            $http.post('http://localhost:8095//acs/elements/'+$scope.startUser.email, JSON.stringify($scope.board)).then(function (response) {
              $scope.Rboard = response.data;
              $scope.apartments_boards.push($scope.Rboard);
             
            });
          });
        });
      }
      }
     
    });
      
    });

  $scope.HH= function(){
    $http.get('http://localhost:8095//acs/users/login/'+$scope.userEmail).then(function (response) {
      $scope.userDetails = response.data;
      if(response.status ='200')
      swal("Login", "login successful!", "success");
      else
      swal("Error", "login successful!", "error");
      $scope.start = 'welcome'
      $scope.show = false
      
    });

  }

  $scope.empty= function(){
    $scope.boardChildren = []

  }

  $scope.emptyMarkers= function(){
    for (i = 0; i < $scope.highlighters.length; i++) {
      $scope.highlighters[i].setMap(null);
    }

  }

  $scope.login= function(){
    $http.get('http://localhost:8095//acs/users/login/'+$scope.userEmail).then(function mySuccess(response) {
      $scope.userDetails = response.data;
      $scope.emptyMarkers()
      $scope.empty()
      if(response.status ='200')
      swal("Login", "login successful!", "success");
      $scope.start = 'welcome'
      $scope.show = false
    }, function myError(response) {
      swal("Error", response.data.message, "error");
    });

  }

  $scope.showUser= function()
  {
    
    $scope.userDetails = {
      email: $scope.userEmail,
      role: $scope.role,
      username:  $scope.name,
      avatar: $scope.avatar,
      };

      $http.post('http://localhost:8095/acs/users', JSON.stringify($scope.userDetails)).then(function mySuccess(response) {
        $scope.userDetails = response.data;
        $scope.emptyMarkers()
        $scope.empty()
        $scope.start = 'welcome'
        $scope.show = false
        
        swal("Regiser", "register successful!", "success");
      }, function myError(response) {
        swal("Error", response.data.message, "error");
      });
  
  }

  $scope.createApartment= function()
  {
    $scope.start = 'welcome'
    $scope.show = false
    $scope.house.type = 'APARTMENT'
    $scope.house.active = false
    $scope.house.createdBy =  $scope.userDetails.email
    $scope.house.elementAttributes.board = $scope.mainBoard.name
    $scope.tempRole = 'MANAGER';
    if($scope.userDetails.role == 'PLAYER'){
      $scope.tempRole = 'PLAYER';
    }

    $scope.updatedUserDetails =$scope.userDetails
    $scope.updatedUserDetails.role = 'MANAGER';
    $http.put('http://localhost:8095//acs/users/'+$scope.userDetails.email, JSON.stringify($scope.updatedUserDetails)).then(function mySuccess(response) {
      $http.post('http://localhost:8095//acs/elements/'+$scope.userDetails.email, JSON.stringify($scope.house)).then(function mySuccess(response) {
        $scope.house = response.data;
        swal("success", "apartment created successfuly!", "success");
        $http.put('http://localhost:8095//acs/elements/'+$scope.userDetails.email+'/'+$scope.mainBoard.elementId+'/children', JSON.stringify({id:$scope.house.elementId})).then(function mySuccess(response) {
          $scope.statuschuldren = response;
          $scope.updatedUserDetails.role = $scope.tempRole;
          $http.put('http://localhost:8095//acs/users/'+$scope.userDetails.email, JSON.stringify($scope.updatedUserDetails)).then(function mySuccess(response) {
            if($scope.tempRole == 'PLAYER'){
              $scope.actionToAdd.type = 'Add element'
              $scope.actionToAdd.element = {elementId:$scope.mainBoard.elementId}
              $scope.actionToAdd.invokedBy = {email:$scope.userDetails.email}
              $http.post('http://localhost:8095//acs/actions', JSON.stringify($scope.actionToAdd)).then(function mySuccess(response) {
                $scope.newAction = response.data;
               // swal("Regiser", "register successful!", "success");
              }, function myError(response) {
                swal("Error", response.data.message, "error");
              });
            }
            
         // swal("user", "change to manager successful!", "success");
        }, function myError(response) {
          swal("Error", response.data.message, "error");
        });
         // swal("apartment", "create successful!", "success");
        }, function myError(response) {
          swal("Error", response.data.message, "error");
        });
         
      }, function myError(response) {
        swal("Error", response.data.message, "error");
      });
         // swal("user", "change to manager successful!", "success");
        }, function myError(response) {
          swal("Error", response.data.message, "error");
        });
   

      
       
        
     
  }

  $scope.getApartmentbyBoard= function(){

    $http.get('http://localhost:8095//acs/elements/'+$scope.userDetails.email+'/'+$scope.board.elementId+'/children').then(function (response) {
      $scope.boardChildren = response.data;
      $scope.emptyMarkers()
      for (i = 0; i < $scope.boardChildren.length; i++) {
        $scope.showOnMAP($scope.boardChildren[i]);
      }
     
    });
  }


  $scope.updateElement= function(element){
   
    element.active =true
    $http.put('http://localhost:8095//acs/elements/'+$scope.userDetails.email+'/'+element.elementId, JSON.stringify(element)).then(function mySuccess(response) {
          $scope.statuschuldren = response;
          for (i = 0; i < $scope.boardChildren.length; i++) {
            $scope.showOnMAP($scope.boardChildren[i]);
          }
          swal("success", "active status changed", "success");
        }, function myError(response) {
          swal("Error", response.data.message, "error");
        });
  }

  $scope.adminFunctiones= function(){

    if($scope.action ==1){
      $http.get('http://localhost:8095//acs/admin/users/'+$scope.userDetails.email).then(function mySuccess(response) {
        $scope.allUsers = response.data;
        $scope.adminTable = 'users';
      }, function myError(response) {
        swal("Error", response.data.message, "error");
      });
    }
   else if($scope.action ==2){
    $http.delete('http://localhost:8095//acs/admin/users/'+$scope.userDetails.email).then(function mySuccess(response) {
      $scope.allactions = response.data;
      $scope.adminTable = 'actions';
      swal("Success", "All users deleted successfuly!", "success");
    }, function myError(response) {
      swal("Error", response.data.message, "error");
    });
    }
    else if($scope.action ==3){
      $http.get('http://localhost:8095//acs/admin/actions/'+$scope.userEmail).then(function mySuccess(response) {
        $scope.allactions = response.data;
        $scope.adminTable = 'actions';
      }, function myError(response) {
        swal("Error", response.data.message, "error");
      });
      }
      else if($scope.action ==4){
        $http.delete('http://localhost:8095//acs/admin/actions/'+$scope.userDetails.email).then(function mySuccess(response) {
          swal("Success", "All actions deleted successfuly!", "success");
        }, function myError(response) {
          swal("Error", response.data.message, "error");
        });
        }
        else if($scope.action ==5){
          $http.delete('http://localhost:8095//acs/admin/elements/'+$scope.userDetails.email).then(function mySuccess(response) {
            swal("Success", "All elements deleted successfuly!", "success");
          }, function myError(response) {
            swal("Error", response.data.message, "error");
          });
          }
        
    
  }

  $scope.newApartment= function(){
    document.getElementById("placeForm").style.visibility = "hidden";
    $scope.update = false
    $scope.house = {};
  }


  $scope.updateScreen= function(location){
    $scope.update = true
    $scope.showOnMAP(location)
    document.getElementById("placeForm").style.visibility = "visible";
    document.getElementById("Map_tab").click();
  }

  $scope.watchMap= function(location){
    $scope.showOnMAP(location)
    document.getElementById("placeForm").style.visibility = "hidden";
    document.getElementById("Map_tab").click();
  }

  $scope.showOnMAP= function(location){
    $scope.house = location
    location.city = location.elementAttributes.reqAddress
    location.desc = location.elementAttributes.reqDescription
    location.lat = location.location.lat
    location.long = location.location.lng
   if (location.active == true){
    $scope.mapIcon =icon 
   }
   else{
    $scope.mapIcon =tempIcon 
   }
   
   
    createHighlighter(location);
    $scope.gMap.setCenter({ lat : location.location.lat, lng :location.location.lng });
    $scope.gMap.setZoom(18);
   
  }
  
  
		//$http.post('http://localhost:80/requests/report/opinion/'+id+'?userOpinion='+opinion).then(function(response) {
     
  $scope.highlighters = [];
  $scope.gMap = null;
   $scope.placeChoose = false;
   document.getElementById("placeForm").style.visibility = "hidden";
   
  var winInfo = new google.maps.InfoWindow();
  
  var googleMapOption = {
    zoom: 15,
    center: new google.maps.LatLng(32.07617098195747, 34.77705094177245),
    mapTypeId: google.maps.MapTypeId.ROADMAP    
  };

  $scope.gMap = new google.maps.Map(document.getElementById('googleMap'), googleMapOption);

  google.maps.event.addListener($scope.gMap, 'click', function(event) {
    $scope.house = {};
    $scope.house.location= {};
    $scope.house.elementAttributes= {};
    $scope.house.location.lat =  event.latLng.lat()
    $scope.house.location.lng = event.latLng.lng()
  placeMarker($scope.gMap, event.latLng);
 document.getElementById("placeForm").style.visibility = "visible";
});

var icon = {
    url: 'home.png', // url
    scaledSize: new google.maps.Size(20, 20), // scaled size
    origin: new google.maps.Point(0,0), // origin
    anchor: new google.maps.Point(0, 0) // anchor
}; 

var tempIcon = {
  url: 'tempHome.png', // url
  scaledSize: new google.maps.Size(20, 20), // scaled size
  origin: new google.maps.Point(0,0), // origin
  anchor: new google.maps.Point(0, 0) // anchor
}; 
function placeMarker(map, location) {
  var marker = new google.maps.Marker({
    position: location,
	icon:tempIcon,
    map: map,
  });
   
   
  google.maps.event.addListener(marker, 'click', function() {
     marker.setMap(null);
	 document.getElementById("placeForm").style.visibility = "hidden";
    });
  var infowindow = new google.maps.InfoWindow({
    content: 'Latitude: ' + location.lat() +
    '<br>Longitude: ' + location.lng()
  });
  infowindow.open(map,marker);
  $scope.highlighters.push(marker);
}



  var createHighlighter = function(citi) {

    var citiesInfo = new google.maps.Marker({
      map: $scope.gMap,
	  icon:$scope.mapIcon,
      position: new google.maps.LatLng(citi.lat, citi.long),
      title: citi.city
    });

    citiesInfo.content = '<div>' + citi.desc + '</div>';
    citiesInfo.customData = citi;

   // google.maps.event.addListener(citiesInfo, 'mouseover', function() {
   //   winInfo.setContent('<h4>' + citiesInfo.title + '</h4>' + citiesInfo.content);
    //  winInfo.open($scope.gMap, citiesInfo);
   // });
	google.maps.event.addListener(citiesInfo, 'click', function() {
    // citiesInfo.setMap(null);
    $http.get('http://localhost:8095//acs/elements/'+$scope.userDetails.email+'/'+citiesInfo.customData.elementId).then(function (response) {
      $scope.house = {};
      $scope.markerHouse = response.data;
      winInfo.setContent('<h4>' + $scope.markerHouse.createdBy.email + '</h4>' + $scope.markerHouse.elementAttributes.phone);
      winInfo.open($scope.gMap, citiesInfo);
      if($scope.userDetails.role == 'PLAYER'){
        $scope.actionToAdd.type = 'show element details'
        $scope.actionToAdd.element = {elementId:$scope.markerHouse.elementId}
        $scope.actionToAdd.invokedBy = {email:$scope.userDetails.email}
        $http.post('http://localhost:8095//acs/actions', JSON.stringify($scope.actionToAdd)).then(function mySuccess(response) {
          $scope.newAction = response.data;
        }, function myError(response) {
          swal("Error", response.data.message, "error");
        });
      }
     
    });
    $scope.house = citiesInfo.customData
    });
    $scope.highlighters.push(citiesInfo);
  };

 
});