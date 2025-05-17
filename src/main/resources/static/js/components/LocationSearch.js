import React, { useState, useEffect } from 'react';
import { GoogleMap, LoadScript, Autocomplete } from '@react-google-maps/api';

const LocationSearch = ({ onLocationSelect }) => {
    const [currentLocation, setCurrentLocation] = useState(null);
    const [searchLocation, setSearchLocation] = useState('');
    const [autocomplete, setAutocomplete] = useState(null);

    useEffect(() => {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                (position) => {
                    const { latitude, longitude } = position.coords;
                    setCurrentLocation({ lat: latitude, lng: longitude });
                    
                    fetch(`/api/geolocation/coordinates?latitude=${latitude}&longitude=${longitude}`)
                        .then(response => response.json())
                        .then(data => {
                            setSearchLocation(data.address);
                            onLocationSelect(data);
                        });
                },
                (error) => {
                    console.error('Erreur de géolocalisation:', error);
                }
            );
        }
    }, []);

    const onLoad = (autocomplete) => {
        setAutocomplete(autocomplete);
    };

    const onPlaceChanged = () => {
        if (autocomplete !== null) {
            const place = autocomplete.getPlace();
            if (place.geometry) {
                const location = {
                    lat: place.geometry.location.lat(),
                    lng: place.geometry.location.lng(),
                    address: place.formatted_address
                };
                setCurrentLocation(location);
                setSearchLocation(place.formatted_address);
                onLocationSelect(location);
            }
        }
    };

    return (
        <div className="location-search">
            <LoadScript
                googleMapsApiKey={process.env.REACT_APP_GOOGLE_MAPS_API_KEY}
                libraries={['places']}
            >
                <Autocomplete
                    onLoad={onLoad}
                    onPlaceChanged={onPlaceChanged}
                >
                    <input
                        type="text"
                        placeholder="Entrez votre adresse"
                        value={searchLocation}
                        onChange={(e) => setSearchLocation(e.target.value)}
                        className="location-input"
                    />
                </Autocomplete>
            </LoadScript>
            
            {currentLocation && (
                <div className="current-location">
                    <p>Position actuelle: {searchLocation}</p>
                    <button 
                        onClick={() => {
                            navigator.geolocation.getCurrentPosition(
                                (position) => {
                                    const { latitude, longitude } = position.coords;
                                    setCurrentLocation({ lat: latitude, lng: longitude });
                                    fetch(`/api/geolocation/coordinates?latitude=${latitude}&longitude=${longitude}`)
                                        .then(response => response.json())
                                        .then(data => {
                                            setSearchLocation(data.address);
                                            onLocationSelect(data);
                                        });
                                }
                            );
                        }}
                        className="refresh-location"
                    >
                        Actualiser ma position
                    </button>
                </div>
            )}
        </div>
    );
};

export default LocationSearch; 