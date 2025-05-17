-- Créer des index spatiaux pour améliorer les performances des requêtes géographiques
CREATE INDEX IF NOT EXISTS rides_departure_coordinates_idx ON rides USING GIST (departure_coordinates);
-- ... même pour les autres

CREATE INDEX rides_arrival_coordinates_idx ON rides USING GIST (arrival_coordinates);
CREATE INDEX rides_route_idx ON rides USING GIST (route);
CREATE INDEX ride_requests_pickup_coordinates_idx ON ride_requests USING GIST (pickup_coordinates);
CREATE INDEX ride_requests_dropoff_coordinates_idx ON ride_requests USING GIST (dropoff_coordinates);