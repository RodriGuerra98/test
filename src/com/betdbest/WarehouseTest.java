package com.betdbest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;


public class WarehouseTest {

  static final String SEMICOLON = ";";
  static final String COLON = ",";
  static final DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm");
  static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.000");

  static final Float EXPERIENCE_PRICE_BY_HOUR = 0.03f;

  static enum Warehouse {
    NEW_YORK(-4), SAN_FRANCISCO(-7);

    private Integer timeZoneOffset;

    Warehouse(int timeZoneOffset) {
      this.timeZoneOffset = timeZoneOffset;
    }

    static Warehouse fromName(String input) {
      switch (input) {
      case "New York":
        return NEW_YORK;
      case "San Francisco":
        return SAN_FRANCISCO;
      }
      return null;
    }

    public String toName() {
      switch (this) {
      case NEW_YORK:
        return "New York";
      case SAN_FRANCISCO:
        return "San Francisco";
      }
      return null;
    }

    public Integer getTimeZoneOffset() {
      return timeZoneOffset;
    }
  }

  static class Stock {
    final String itemId;
    final Warehouse warehouse;
    final int stock;

    Stock(String itemId, Warehouse warehouse, int stock) {
      this.itemId = itemId;
      this.warehouse = warehouse;
      this.stock = stock;
    }

    public String getItemId() {
      return itemId;
    }

    public Warehouse getWarehouse() {
      return warehouse;
    }

    public int getStock() {
      return stock;
    }

  }

  static class BoxType {
    final String boxType;
    final int maxWeight;
    final int length, width, height; // cm
    final float volume; // dm3

    BoxType(String boxType, int maxWeight, int length, int width, int height, float volume) {
      this.boxType = boxType;
      this.maxWeight = maxWeight;
      this.length = length;
      this.width = width;
      this.height = height;
      this.volume = volume;
    }

    public String getBoxType() {
      return boxType;
    }

    public int getMaxWeight() {
      return maxWeight;
    }

    public int getLength() {
      return length;
    }

    public int getWidth() {
      return width;
    }

    public int getHeight() {
      return height;
    }

    public float getVolume() {
      return volume;
    }

  }

  static class CarrierPricing {
    final Warehouse warehouse;
    final String targetState;
    final float volumePrice; // $/dm3

    CarrierPricing(Warehouse warehouse, String targetState, float volumePrice) {
      this.warehouse = warehouse;
      this.targetState = targetState;
      this.volumePrice = volumePrice;
    }

    public Warehouse getWarehouse() {
      return warehouse;
    }

    public String getTargetState() {
      return targetState;
    }

    public float getVolumePrice() {
      return volumePrice;
    }

  }

  static class ShippingHour {
    final DayOfWeek day;
    final LocalTime time;

    ShippingHour(DayOfWeek day, LocalTime time) {
      this.time = time;
      this.day = day;
    }

    public DayOfWeek getDay() {
      return day;
    }

    public LocalTime getTime() {
      return time;
    }

  }

  static class DepartureTime {
    final Warehouse warehouse;
    final String targetState;
    final List<ShippingHour> shippingHours;

    DepartureTime(Warehouse warehouse, String targetState, List<ShippingHour> shippingHours) {
      this.warehouse = warehouse;
      this.targetState = targetState;
      this.shippingHours = shippingHours;
    }

    public Warehouse getWarehouse() {
      return warehouse;
    }

    public String getTargetState() {
      return targetState;
    }

    public List<ShippingHour> getShippingHours() {
      return shippingHours;
    }
  }

  static class CarrierTime {
    final Warehouse warehouse;
    final String targetState;
    final int carrierTime; // in hours

    CarrierTime(Warehouse warehouse, String targetState, int carrierTime) {
      this.warehouse = warehouse;
      this.targetState = targetState;
      this.carrierTime = carrierTime;
    }

    public Warehouse getWarehouse() {
      return warehouse;
    }

    public String getTargetState() {
      return targetState;
    }

    public int getCarrierTime() {
      return carrierTime;
    }

  }

  static class Item {
    final String itemId;
    final int weight;
    final int length, width, height;

    Item(String itemId, int weight, int length, int width, int height) {
      this.itemId = itemId;
      this.weight = weight;
      this.length = length;
      this.width = width;
      this.height = height;
    }

    public String getItemId() {
      return itemId;
    }

    public int getLength() {
      return length;
    }

    public int getWeight() {
      return weight;
    }

    public int getWidth() {
      return width;
    }

    public int getHeight() {
      return height;
    }

  }

  static class Order {
    final long orderId;
    final LocalDateTime orderDate;
    final String itemId;
    final String targetState;

    Order(long orderId, LocalDateTime orderDate, String itemId, String targetState) {
      this.orderId = orderId;
      this.itemId = itemId;
      this.orderDate = orderDate;
      this.targetState = targetState;
    }

    public long getOrderId() {
      return orderId;
    }

    public LocalDateTime getOrderDate() {
      return orderDate;
    }

    public String getItemId() {
      return itemId;
    }

    public String getTargetState() {
      return targetState;
    }

  }

  static class ShipmentInfo {
    final Order order;
    final Warehouse warehouse;
    final LocalDateTime guaranteedDeliveryDate;
    final String boxType;
    final float shippingPrice;

    ShipmentInfo(Order order, Warehouse warehouse, LocalDateTime guaranteedDeliveryDate, String boxType,
        float shippingPrice) {
      this.order = order;
      this.warehouse = warehouse;
      this.guaranteedDeliveryDate = guaranteedDeliveryDate;
      this.boxType = boxType;
      this.shippingPrice = shippingPrice;
    }

    public Order getOrder() {
      return order;
    }

    public String getItemId() {
      return order.itemId;
    }

    public Warehouse getWarehouse() {
      return warehouse;
    }

    public LocalDateTime getGuaranteedDeliveryDate() {
      return guaranteedDeliveryDate;
    }

    public String getBoxType() {
      return boxType;
    }

    public float getShippingPrice() {
      return shippingPrice;
    }

    public String toCsvLine() {
      return new StringBuilder().append(order.orderId).append(SEMICOLON).append(warehouse.toName()).append(SEMICOLON)
          .append(DATE_PATTERN.format(guaranteedDeliveryDate)).append(SEMICOLON).append(boxType).append(SEMICOLON)
          .append(DECIMAL_FORMAT.format(shippingPrice)).append(SEMICOLON)
          .append(DECIMAL_FORMAT.format(getShippingExperiencePrice())).toString();
    }

    public Float getShippingExperiencePrice() {
      long hours = order.orderDate.until(guaranteedDeliveryDate, ChronoUnit.HOURS);
      return hours * EXPERIENCE_PRICE_BY_HOUR;
    }

    public Float getTotalPrice() {
      return getShippingPrice() + getShippingExperiencePrice();
    }
  }

  static class CsvParser {

    public static final Order parseOrder(String inputLine) {
      String[] input = inputLine.split(SEMICOLON);
      LocalDateTime orderDate = LocalDateTime.parse(input[1], DATE_PATTERN);
      return new Order(Long.valueOf(input[0]), orderDate, input[2], input[4]);
    }

    public static final Stock parseStock(String inputLine) {
      String[] input = inputLine.split(SEMICOLON);
      return new Stock(input[0], Warehouse.fromName(input[1]), Integer.valueOf(input[2]));
    }

    public static final BoxType parseBoxType(String inputLine) {
      String[] input = inputLine.split(SEMICOLON);
      return new BoxType(input[0], Integer.valueOf(input[1]), Integer.valueOf(input[2]), Integer.valueOf(input[3]),
          Integer.valueOf(input[4]), Float.valueOf(input[5].replaceAll(",", ".")));
    }

    public static final CarrierPricing parseCarrierPricings(String inputLine) {
      String[] input = inputLine.split(SEMICOLON);
      String costString = input[2];
      return new CarrierPricing(Warehouse.fromName(input[0]), input[1], Float.valueOf(costString.replaceAll(",", ".")));
    }

    public static final DepartureTime parseDepartureTime(String inputLine) {
      String[] input = inputLine.split(SEMICOLON);
      Warehouse warehouse = Warehouse.fromName(input[0]);

      String departureTimes[] = input[2].split(COLON);
      List<ShippingHour> shippingHours = Arrays.stream(departureTimes).map(new Function<String, ShippingHour>() {

        @Override
        public ShippingHour apply(String departureTime) {
          String[] values = departureTime.trim().split(" ");
          DayOfWeek dayOfWeek = DayOfWeek.valueOf(values[0]);
          LocalTime localTime = LocalTime.parse(values[1]);
          return new ShippingHour(dayOfWeek, localTime);
        }
      }).collect(Collectors.toList());
      return new DepartureTime(warehouse, input[1], shippingHours);
    }

    public static final CarrierTime parseCarrierTime(String inputLine) {
      String[] input = inputLine.split(SEMICOLON);
      return new CarrierTime(Warehouse.fromName(input[0]), input[1], Integer.valueOf(input[2].split(" ")[0]));
    }

    public static final Item parseItem(String inputLine) {
      String[] input = inputLine.split(SEMICOLON);
      return new Item(input[0], Integer.valueOf(input[2]), Integer.valueOf(input[3]), Integer.valueOf(input[4]),
          Integer.valueOf(input[5]));
    }
  }

  static class ShipmentsManager {

    final Integer PACKAGE_PREPARATION_HOURS = 4;

    static class NoSuitableBoxException extends RuntimeException {
      private static final long serialVersionUID = 7513400494522133911L;

      public NoSuitableBoxException(String itemId) {
        super("There is no suitable for item " + itemId);
      }
    }

    static class NoSuitableWarehouseException extends RuntimeException {
      private static final long serialVersionUID = 7513400494522133911L;

      public NoSuitableWarehouseException(String itemId, String targetState) {
        super("There is no warehouse with stock and deliver options for item " + itemId + " and target state "
            + targetState);
      }
    }
    
    private List<Item> items;
    private List<BoxType> boxTypes;
    private List<Stock> stocks;
    private List<CarrierPricing> pricings;
    private List<CarrierTime> times;
    private List<DepartureTime> departures;

    public ShipmentsManager(List<Item> items, List<BoxType> boxTypes, List<CarrierPricing> carrierPricings,
        List<DepartureTime> departureTimes, List<CarrierTime> carrierTimes, List<Stock> initialStocks) {
      this.items = items;
      this.boxTypes = boxTypes;
      this.pricings = carrierPricings;
      this.departures = departureTimes;
      this.times = carrierTimes;
      this.stocks = initialStocks;
    }
    
    public ShipmentInfo findBestShipmentInfo(Order order) throws NoSuitableWarehouseException, NoSuitableBoxException {
      ArrayList<Warehouse> availableWarehouses = new ArrayList<>();

      // First of all we check if we have stock in all of our warehouses for the given
      // order
      for (Stock stock : this.stocks) {
        if ((stock.getItemId().equals(order.getItemId())) && (stock.getStock() > 0)) {
          availableWarehouses.add(stock.getWarehouse());
        }
        // Avoid extra iterations...
        if (availableWarehouses.size() == Warehouse.values().length)
          break;
      }

      if (availableWarehouses.size() == 0) {
        throw new NoSuitableWarehouseException(order.getItemId(), order.getTargetState());
      }
      BoxType boxType = findBestBoxType(order);
      ShipmentInfo info = findBestRoute(availableWarehouses, order, boxType);

      if (info == null)
        throw new NoSuitableWarehouseException(order.getItemId(), order.getTargetState());

      decreaseStock(info.getWarehouse(), order);

      return info;
    }
    
    private ShipmentInfo findBestRoute(List<Warehouse> warehouses, Order order, BoxType box)
        throws NoSuitableWarehouseException {
    	List<ShipmentInfo> listaShip = new ArrayList<>();        // Lista de shipmenteInfo para calcular el precio y ver cual es la m�s barata
    	ShipmentInfo finalShipment=null;						 // Objeto que vamos a utilizar para almacenar el shipmentInfo m�s barato
    	
    	List<CarrierPricing> cPs = getCPs(warehouses, order);	 // Lista de CarrierPricings disponibles para dichos warehouses y targetState
    	
		for(CarrierPricing cpFinal : cPs) {
			float shippingPrice;								 // Precio de volumen *volumePrice de cada carrierPricings								
			ShipmentInfo shipInfo;								 // Objeto auxiliar con el que obtener el totalPrice para guardarlo en la lista "listaShip"
			LocalDateTime guaranteedTm;							 // Fecha necesaria para calcular el ShipmentExperiencePrice
			ShipmentInfo shipFinal = null;						 // Objeto para sacar el shipmentInfo mas barato dentro de un CarrierPricing
			List<ShipmentInfo> shipLista = new ArrayList<>();	 // Lista de shipmentInfo para sacar el m�s barato
			
			CarrierTime carrierTime = getCarrierTime(cpFinal);   // CarrierTime mas bajo
			
			try{
				shippingPrice = (float) (cpFinal.getVolumePrice() * box.getVolume()); 
			}catch(Exception e){
				System.out.println("Error al operar el precio final");
				return null;
			}
			for(DepartureTime departure: departures) {
				if(departure.getWarehouse().equals(cpFinal.getWarehouse())  
						&& departure.getTargetState().equals(cpFinal.getTargetState()) ) { 
					List<ShippingHour>shHours = departure.getShippingHours();
					for(ShippingHour shAux : shHours) {
						try {
							guaranteedTm = getDeliveryDateTime(order.getOrderDate(), shAux, carrierTime);	
						}catch(Exception e) {
							System.out.println("Error al calcular el guaranteedTime");
							return null;
						}
						try {
							shipInfo = new ShipmentInfo(order, cpFinal.getWarehouse(), guaranteedTm, box.getBoxType(), shippingPrice);
							shipLista.add(shipInfo);
						}catch(Exception e) {
							System.out.println("Error al crear el shipment info");
							return null;
						}
					}
					int size_t = shipLista.size();					
					shipFinal = shipLista.get(0);
					for (int i =1 ; i < size_t ; i++) {
						if( shipFinal.getTotalPrice() > shipLista.get(i).getTotalPrice()) {
							shipFinal = shipLista.get(i); 
						}
					}
				}
			}
			listaShip.add(shipFinal);
			for(ShipmentInfo s : listaShip) {
				if(finalShipment == null || finalShipment.getTotalPrice() > s.getTotalPrice()) {
					finalShipment = s;
				}
			}
		}
      return finalShipment;
    }

    /*Funcion para coger el menor carrier time que corresponda a los warehouses disponibles y el estado destino*/
	private CarrierTime getCarrierTime(CarrierPricing cpFinal) {
		CarrierTime carrierTime=null;
		for(CarrierTime crTimes : this.times) {
			if(cpFinal.getWarehouse().equals(crTimes.getWarehouse() )&&
					cpFinal.getTargetState().equals(crTimes.getTargetState()) ) {
				if(carrierTime==null || carrierTime.getCarrierTime() > crTimes.getCarrierTime()) {
					carrierTime = crTimes;
				}
			}	
		}
		return carrierTime;
	}
	
	/*Funcion para coger la lista de CarrierPricings que corresponda a los warehouses disponibles y el estado destino*/
	private List<CarrierPricing> getCPs(List<Warehouse> warehouses, Order order) {
		List<CarrierPricing> cPs = new ArrayList<>();
		for(Warehouse wh : warehouses) {
			for(CarrierPricing cp : this.pricings){
				if(cp.getWarehouse().equals(wh) && cp.getTargetState().equals(order.getTargetState())) {
					cPs.add(cp);
				}
			}
		}
		return cPs;
	}
    
    private BoxType findBestBoxType(Order order) throws NoSuitableBoxException {
    	String itemId;
    	itemId = order.getItemId();	//Guardamos el itemId en una variable para hacerlo m�s sencillo
    	BoxType cajaFinal = null;	//Caja que vamos a seleccionar
    	for(Item item : items) {
    		if (item.getItemId().equals(itemId)) { 						//Obtenemos el item por el id del order
    			ArrayList<BoxType> cajasPeso = getBoxesForWeight(item);
    			if(cajasPeso.isEmpty()) {
    				System.out.println("El peso del producto es demasiado grande para las cajas");
    			}else {                                             
    				int height,width, length;
    				width   = item.getWidth();		//Ancho del producto
    				length  = item.getLength();		//Largo del producto
    				height  = item.getHeight();		//Alto  del producto
    
    				ArrayList<BoxType> cajasDimension = getBoxesForDimension(cajasPeso, height, width, length);    //Lsta de cajas que aceptan la dimension
    				int size = cajasDimension.size(); //tama�o de la lista
    				
    				cajaFinal = cajasDimension.get(0); 
    				for (int i =1 ; i < size ; i++) {
						if( cajaFinal == null ||cajaFinal.getVolume() > cajasDimension.get(i).getVolume()) {
							cajaFinal = cajasDimension.get(i);
						}
    				}
    			}	
    		}
    	}
    	
      return cajaFinal;
    }
    /*Funcion que devuelve la lista de cajas que pueden almacenar el item por dimension, no distingue entre largo y ancho ya que se puede girar en el plano X*/
	private ArrayList<BoxType> getBoxesForDimension(ArrayList<BoxType> cajasPeso, int height, int width, int length) {
		ArrayList<BoxType> cajasDimension = new ArrayList<>();
		
		for(BoxType cajaAux: cajasPeso) {
			if(cajaAux.getHeight() >= height 
					&& ((cajaAux.getLength() >= length && cajaAux.getWidth() >= width) || (cajaAux.getWidth() >= length && cajaAux.getLength() >= width))) {
				cajasDimension.add(cajaAux);
			}
		}
		return cajasDimension;
	}
	
	/*Funcion que devuelve la lista de cajas que pueden almacenar el item por peso*/
	private ArrayList<BoxType> getBoxesForWeight(Item item) {
		int peso;
		ArrayList<BoxType> cajasPeso = new ArrayList<>();
		peso = item.getWeight();
		
		for(BoxType caja : boxTypes){ 						// Obtenemos las cajas que aceptan el peso y las a�ado a una lista
			if(caja.getMaxWeight() >= peso) {
				cajasPeso.add(caja);
			}
		}
		return cajasPeso;
	}

    private LocalDateTime getDeliveryDateTime(LocalDateTime orderDate, ShippingHour shippingHour, CarrierTime time) {
      // From this hour we can send the order
      LocalDateTime startDate = orderDate.plusHours(PACKAGE_PREPARATION_HOURS);
      startDate = startDate.plusHours(time.getWarehouse().getTimeZoneOffset());

      // Next day we can ship the order
      LocalDateTime nextDeliveryDay = startDate.with(TemporalAdjusters.next(shippingHour.getDay()))
          .withHour(shippingHour.getTime().getHour()).withMinute(shippingHour.getTime().getMinute());

      int days = (int) startDate.until(nextDeliveryDay, ChronoUnit.DAYS);

      if (days == 7 && (nextDeliveryDay.getHour() * 60 + nextDeliveryDay.getMinute()) >= (startDate.getHour() * 60
          + startDate.getMinute())) { // We can send the day of the order
        LocalDateTime arrivalDate = startDate.withHour(shippingHour.getTime().getHour())
            .withMinute(shippingHour.getTime().getMinute()) // Today at the hour the carrier leaves
            .plusHours(time.getCarrierTime()).minusHours(time.getWarehouse().getTimeZoneOffset()); // Plus offset
        return arrivalDate;
      } else { // Send it ASAP
        return nextDeliveryDay.plusHours(time.getCarrierTime()).minusHours(time.getWarehouse().getTimeZoneOffset());
      }
    }
    
    /**
     * Overwrites our stock of the Order's item with a unit less
     */
    private void decreaseStock(Warehouse warehouse, Order order) {
      for (Stock stock : this.stocks) {
        if (stock.getWarehouse() != warehouse)
          continue;
        if (stock.getItemId().equals(order.getItemId())) {
          int s = stock.getStock();
          --s;
          this.stocks.set(this.stocks.indexOf(stock), new Stock(stock.getItemId(), warehouse, s));
          break;
        }
      }
    }
  }
  
  

  public static void main(String[] args) throws IOException {
    List<Stock> stocks = new ArrayList<>();
    List<BoxType> boxTypes = new ArrayList<>();
    List<CarrierPricing> carrierPricings = new ArrayList<>();
    List<DepartureTime> departureTimes = new ArrayList<>();
    List<CarrierTime> carrierTimes = new ArrayList<>();
    List<Item> items = new ArrayList<>();

    List<Order> orders = new ArrayList<>();

    Consumer<String> stockConsumer = input -> stocks.add(CsvParser.parseStock(input));
    Consumer<String> boxTypeConsumer = input -> boxTypes.add(CsvParser.parseBoxType(input));
    Consumer<String> carrierPricingConsumer = input -> carrierPricings.add(CsvParser.parseCarrierPricings(input));
    Consumer<String> departureTimeConsumer = input -> departureTimes.add(CsvParser.parseDepartureTime(input));
    Consumer<String> carrierTimeConsumer = input -> carrierTimes.add(CsvParser.parseCarrierTime(input));
    Consumer<String> itemConsumer = input -> items.add(CsvParser.parseItem(input));
    Consumer<String> orderConsumer = input -> orders.add(CsvParser.parseOrder(input));

    // BufferedWriter bw = new BufferedWriter(new
    // FileWriter(System.getenv("OUTPUT_PATH")));
    BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"));
    // BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    BufferedReader br = new BufferedReader(new FileReader("input.txt"));

    String inputLine;
    Consumer<String> consumer = t -> {
    };
    while ((inputLine = br.readLine()) != null) {
      switch (inputLine) {
      case "---Orders---":
        consumer = orderConsumer;
        break;
      case "---Stocks---":
        consumer = stockConsumer;
        break;
      case "---BoxTypes---":
        consumer = boxTypeConsumer;
        break;
      case "---CarrierPricing---":
        consumer = carrierPricingConsumer;
        break;
      case "---DepartureTimes---":
        consumer = departureTimeConsumer;
        break;
      case "---CarrierTimes---":
        consumer = carrierTimeConsumer;
        break;
      case "---Items---":
        consumer = itemConsumer;
        break;
      default:
        consumer.accept(inputLine);
        break;
      }
    }
    br.close();

    Collections.sort(orders, new Comparator<Order>() {
      @Override
      public int compare(Order arg0, Order arg1) {
        return arg0.getOrderDate().compareTo(arg1.getOrderDate());
      }
    });

    ShipmentsManager shipmentsManager = new ShipmentsManager(items, boxTypes, carrierPricings, departureTimes,
        carrierTimes, stocks);

    List<ShipmentInfo> shipmentInfos = orders.stream().map(shipmentsManager::findBestShipmentInfo)
        .collect(Collectors.toList());

    Collections.sort(shipmentInfos, new Comparator<ShipmentInfo>() {
      @Override
      public int compare(ShipmentInfo arg0, ShipmentInfo arg1) {
        return arg0.getOrder().getOrderDate().compareTo(arg1.getOrder().getOrderDate());
      }
    });

    Float totalShipmentPrice = 0.0f;

    for (ShipmentInfo shipmentInfo : shipmentInfos) {
      totalShipmentPrice += shipmentInfo.shippingPrice + shipmentInfo.getShippingExperiencePrice();
    }
    StringBuilder output = new StringBuilder();
    output.append(totalShipmentPrice + "\n");
    for (ShipmentInfo shipmentInfo : shipmentInfos) {
      output.append(shipmentInfo.toCsvLine() + "\n");
    }
    bw.write(output.toString());
    bw.close();
    System.out.println("Your total shipment price is: " + totalShipmentPrice);
  };

}
