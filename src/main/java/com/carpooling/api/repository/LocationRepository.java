package com.carpooling.api.repository;

import com.carpooling.api.entity.Location;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    
    @Query(value = "SELECT * FROM locations l WHERE ST_DWithin(coordinates, :point, :radius)", nativeQuery = true)
    List<Location> findWithinRadius(@Param("point") Point point, @Param("radius") double radius);

    @Query(value = "SELECT * FROM locations l ORDER BY ST_Distance(coordinates, :point)", nativeQuery = true)
    List<Location> findNearestLocations(@Param("point") Point point);

    @Query(value = "SELECT * FROM locations l WHERE ST_Within(coordinates, :polygon)", nativeQuery = true)
    List<Location> findWithinPolygon(@Param("polygon") Geometry polygon);

    @Query(value = "SELECT * FROM locations l WHERE ST_DWithin(coordinates, :route, :distance)", nativeQuery = true)
    List<Location> findAlongRoute(@Param("route") Geometry route, @Param("distance") double distance);
}