(function ($) {
    // Define a Vue instance for search results
    var search_result = new Vue({
        el: '#search-result',
        data: {
            search_result: []
        },
        methods: {}
    });

    // Function to handle the search button click
    $("#searchButton").click(function () {
        const query = $("#searchBox").val();
        $.get(`/restaurant/search?target=${query}&page=0&limit=350`, function (response) {
            // Update the search_result data property with the search results
            search_result.search_result = response.content;

            // Call initializeMap to update markers on the map
            initializeMap();
        });
    });

    // Enter key press handling (optional)
    $("#searchBox").keydown(function (key) {
        if (key.keyCode === 13) {
            const query = $("#searchBox").val();
            $.get(`/restaurant/search?target=${query}&page=0&limit=300`, function (response) {
                // Update the search_result data property with the search results
                search_result.search_result = response.content;

                // Call initializeMap to update markers on the map
                initializeMap();
            });
        }
    });

    // Initialize the map when the Naver Map library is ready
    naver.maps.onJSContentLoaded = function () {
        // Don't initialize the map here; it will be initialized after fetching and processing the results
    };

    function initializeMap() {
        // Create a map object centered at the average position of markers
        var map = new naver.maps.Map('map', {
            center: getAverageMarkerPosition(),
            zoom: 15
        });

        var markers = [];

        // Function to create a marker and infowindow for a result
        function createMarker(result) {
            var marker = new naver.maps.Marker({
                position: new naver.maps.LatLng(result.mapY, result.mapX),
                map: map,
                title: result.name,
            });

            markers.push(marker);

            var contentString = `
                <div class="iw_inner">
                    <p><b>${result.name}</b><br />
                    <b>주소: </b>${result.address}<br />
                    </p>
                </div>`;

            var infowindow = new naver.maps.InfoWindow({
                content: contentString,
                maxWidth: 160,
                backgroundColor: "#eee",
                borderColor: "#2db400",
                borderWidth: 5,
                anchorSize: new naver.maps.Size(30, 30),
                anchorSkew: true,
                anchorColor: "#eee",
                pixelOffset: new naver.maps.Point(20, -20)
            });

            naver.maps.Event.addListener(marker, "click", function () {
                if (infowindow.getMap()) {
                    infowindow.close();
                } else {
                    infowindow.open(map, marker);
                }
            });
        }

        // Loop through each search result and create a marker and infowindow
        for (var i = 0; i < search_result.search_result.length; i++) {
            createMarker(search_result.search_result[i]);
        }
    }

    function getAverageMarkerPosition() {
        var totalLat = 0;
        var totalLng = 0;

        for (var i = 0; i < search_result.search_result.length; i++) {
            var result = search_result.search_result[i];
            totalLat += parseFloat(result.mapY);
            totalLng += parseFloat(result.mapX);
        }

        var averageLat = totalLat / search_result.search_result.length;
        var averageLng = totalLng / search_result.search_result.length;

        return new naver.maps.LatLng(averageLat, averageLng);
    }

    $(document).ready(function () {
        console.log("init");

        // "지역별 맛집 검색" 링크 클릭 이벤트 처리
        $("#influencerSectionLink").click(function (event) {
            event.preventDefault(); // 기본 링크 동작을 막습니다.

            // 이동할 URL 설정
            var newURL = "http://localhost:8080/matprint/named";

            // 페이지를 새 URL로 이동합
            window.location.href = newURL;
        });
    });

})(jQuery);