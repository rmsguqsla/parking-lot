package com.zerobase.parkinglot.parkinglot.repository;


import com.zerobase.parkinglot.error.ErrorCode;
import com.zerobase.parkinglot.parkinglot.entity.ParkingLot;
import com.zerobase.parkinglot.parkinglot.exception.ParkingLotException;
import com.zerobase.parkinglot.parkinglot.model.ParkingLotDto;
import com.zerobase.parkinglot.parkinglot.model.ParkingLotUserInfo;
import com.zerobase.parkinglot.parkinglot.type.SearchType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

@Repository
@RequiredArgsConstructor
public class ParkingLotCustomRepository {

    private final EntityManager entityManager;

    public List<ParkingLotUserInfo> findAllByDistanceLimit20(double myLat, double myLng) {
        String query = "SELECT p.id, p.address, p.name, p.space_count, "
            + "(6371 * acos(cos(radians(p.lat))*cos(radians("+ myLat +"))"
            + "*cos(radians(p.lng)-radians(" + myLng + "))"
            + "+sin(radians(p.lat))*sin(radians("+ myLat +")))) AS distance "
            + "from parking_lot p "
            + "where p.use_yn = 1 "
            + "ORDER BY distance "
            + "LIMIT 0 , 20";

        List<ParkingLotUserInfo> list = entityManager.createNativeQuery(query).getResultList();

        return list;
    };

    public List<ParkingLotUserInfo> findAllBySearch(double myLat, double myLng, String searchType, String searchValue) {

        if (!InvalidSearchType(searchType)) {
            return Collections.EMPTY_LIST;
        }

        String query = "SELECT p.id, p.address, p.name, p.space_count, "
            + "(6371 * acos(cos(radians(p.lat))*cos(radians("+ myLat +"))"
            + "*cos(radians(p.lng)-radians(" + myLng + "))"
            + "+sin(radians(p.lat))*sin(radians("+ myLat +")))) AS distance "
            + "from parking_lot p "
            + "where p.use_yn = 1 ";

        if(SearchType.PARKING_LOT_NAME.getDescription().equals(searchType)) {
            query += "and p.name like concat('%', '" + searchValue + "', '%') ";
        } else if (SearchType.PARKING_LOT_ADDRESS.getDescription().equals(searchType)) {
            query += "and p.address like concat('%', '" + searchValue + "', '%') ";
        }

        query += "ORDER BY distance";

        List<ParkingLotUserInfo> list = entityManager.createNativeQuery(query).getResultList();

        return list;

    }

    private boolean InvalidSearchType(String searchType) {

        boolean exist = false;

        for (SearchType type : SearchType.values()) {
            if (type.getDescription().equals(searchType)) {
                exist = true;
                break;
            }
        }

        return exist;
    }
}
