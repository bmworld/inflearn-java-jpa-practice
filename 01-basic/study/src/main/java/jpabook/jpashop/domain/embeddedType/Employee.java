package jpabook.jpashop.domain.embeddedType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Employee {

    @Id
    @GeneratedValue
    @Column(name = "EMPLOYEE_ID" )
    private Long id;


    @Column(name = "username")
    private String username;


    @Embedded
    private Period period;


    @Embedded
    private Address homeAddress;



    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "city", column = @Column(name = "WORK_CITY")),
            @AttributeOverride(name = "street", column = @Column(name = "WORK_STREET")),
            @AttributeOverride(name = "zipcode", column = @Column(name = "WORK_ZIPCODE"))
    })
    private Address workAddress;



    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "EMPLOYEE_ID")
    private List<AddressEntity> addressHistory = new ArrayList<>();


//
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "FAVORITE_FOOD",
            joinColumns = @JoinColumn(name = "EMPLOYEE_ID")
    )
    @Column(name = "FOOD_NAME") // 컬럼이름을 지정하는 이유는, 엠베디드타입이 아니고, value가 하나이기 때문.
    private Set<String> favoriteFoods = new HashSet<>();





    // 값 타입 Colletion은 실무에서 사용하지 마시라.
    // Why? 값 타입은 식별자 개념이 없으므로, 변경사항 추적 어려움 등의 SideEffect가 있다.
    // 따라서, @OneToMany 방식으로 Embedded 값타임을 Collection으로 사용하시라.
//    @ElementCollection
//    @CollectionTable(
//            name = "ADDRESS",
//            joinColumns = @JoinColumn(name = "EMPLOYEE_ID")
//    )
//    private List<Address> addressHistory = new ArrayList<>();



    public List<AddressEntity> getAddressHistory() {
        return addressHistory;
    }

    public void setAddressHistory(List<AddressEntity> addressHistory) {
        this.addressHistory = addressHistory;
    }




    public Set<String> getFavoriteFoods() {
        return favoriteFoods;
    }

    public void setFavoriteFoods(Set<String> favoriteFoods) {
        this.favoriteFoods = favoriteFoods;
    }

//    public List<Address> getAddressHistory() {
//        return addressHistory;
//    }
//
//    public void setAddressHistory(List<Address> addressHistory) {
//        this.addressHistory = addressHistory;
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }


    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }

    public Address getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(Address workAddress) {
        this.workAddress = workAddress;
    }
}
