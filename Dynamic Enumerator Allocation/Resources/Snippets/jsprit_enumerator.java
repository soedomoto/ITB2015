VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("enumerator");
//vehicleTypeBuilder.addCapacityDimension(0, 100);
//vehicleTypeBuilder.setCostPerDistance(1.0);
vehicleTypeBuilder.setCostPerDistance(0);
vehicleTypeBuilder.setCostPerTransportTime(1);
vehicleTypeBuilder.setCostPerServiceTime(1);
VehicleType vehicleType = vehicleTypeBuilder.build();

try {
    CSVReader reader = new CSVReader(new FileReader(csvEnum));
    reader.readNext();

    String [] line;
    while ((line = reader.readNext()) != null) {
        VehicleImpl.Builder builder = VehicleImpl.Builder.newInstance(line[0]);

        try {
            Location loc = Location.Builder.newInstance()
                    .setId(line[0])
                    .setCoordinate(Coordinate.newInstance(Double.parseDouble(line[2]),
                            Double.parseDouble(line[1])))
                    .build();
            builder.setStartLocation(loc);
        } catch (Exception e) {}


        builder.setType(vehicleType);
        VehicleImpl vehicle = builder.build();
        vrpBuilder.addVehicle(vehicle);
    }

} catch (FileNotFoundException e) {
    e.printStackTrace();
} catch (IOException e) {
    e.printStackTrace();
}
