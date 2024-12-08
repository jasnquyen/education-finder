package cs1302.api;

/** Collects data on schools.
 *
 */

public class Schools {
    private final String name;
    private final String address;
    private final String city;

    /** Method to construct data on schools.
     * @param name
     * @param address
     * @param city
     */

    public Schools(String name, String address, String city) {
        this.name = name;
        this.address = address;
        this.city = city;
    }

    /** Get name of school.
     * @return String
     */

    public String getName() {
        return name;
    }
    /** Return address.
     * @return String
     */

    public String getAddress() {
        return address;
    }

    /** Return city name.
     * @return String
     */

    public String getCity() {
        return city;
    }

    @Override
    public String toString() {
        return name + " (" + address + ") ";
    }

}
