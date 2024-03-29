# <center>How to fix working code</center>
<center>Evgeni Dimitrov</center>

# Safe Harbour
If you use the following and fail, I don't care.


# Overview

## Why it's important

* For Developers - reduces time to fix bugs and implemnt new features. Reduces headaches.
* For Clients - reduces time to market. Reduces time to bring the system back online.
* For Managment - see above

## The start defines the end

If the foundation is weak the rest cannot be solid.
```
      *******
    ***
      **
      *
   ********
```

Broken window principle.
```
   **********
   **********
   **********
   **********
```
## When to reafactor
The biggest problem with code is when it works!

1. Right after the code is working and the unit tests are done.
2. In the scope of a relatively big change.
3. Before the testing has started.

## Questions to answer
* Is code easy to read?
* Is code easy to unit test? Code that is easy to test is easy to reason about.
* Is it easy to find bugs and chnage functionality?

# Sonarqube
https://www.sonarqube.org/

* For the IDE
* As a server application

# Names

## Names provide context

```java
boolean isOperationApproved = true;
```
## Scope
Variable names should be proportional to their scope 

```java
for(int i = 0; i < 10; i++) {
   ...
}
```
Avoid single letter variables 
   exceptions: lambdas and very short methods
   
   Avoid letters “l” and “O” as a variable names

```java
.map(l -> l.getParent())
```

## Use Intention-Revealing Names

```diff
- int elapsed; // elapsed time in days
+ int elapsedTimeInDays;
```

```diff
- List<Person> listOfPeople;
+ List<Person> owners;
```

## Make Meaningful Distinctions

```diff
- void copyChars(char a1[], char a2[]) {
+ void copyChars(char source[],char destination[]) {
```

## Avoid Mental Mapping and "encription"

Don't force readers of the code to remember something
```
l = leftIndex
r = rightIndex
x = the item that have to be found
```
```diff
-int binarySearch(int arr[], int l, int r, int x) {
-   if (r >= l) {
-       int mid = l + (r - l) / 2;
-       if (arr[mid] == x)
-           return mid;
-       if (arr[mid] > x)
-           return binarySearch(arr, l, mid - 1, x);
-       return binarySearch(arr, mid + 1, r, x);
-   }
-   return -1;
-}
+int binarySearch(int arr[], int leftIndex, int rightIndex, int keyToFind) {
+   if (rightIndex >= leftIndex) {
+       int mid = leftIndex + (rightIndex - leftIndex) / 2;
+       if (arr[mid] == keyToFind)
+           return mid;
+       if (arr[mid] > keyToFind)
+           return binarySearch(arr, leftIndex, mid - 1, keyToFind);
+       return binarySearch(arr, mid + 1, rightIndex, keyToFind);
+   }
+   return -1;
+}

```

```diff
-if(machine.getKind() == 0) {
-   ...
-}

-if(machine.getKind() == MachineKind.METAL_CUTTING.getValue()) { // better
-   ...
-}

+if(machine.getKind() == MachineKind.METAL_CUTTING) { // best - limit the state representation of the kind filed by makeing it an enum instead of a number
+   ...
+}

```

> ## Offtopic Mental mapping -  Document things that have to be known
>
> Document it once or explain it 100 times.
>
> `README.md` or even a folder full of readmes
> 
> * Branching strategy (Git Flow / Trunk based development)
> * How to setup a development environmnt
>    * Databse instalation (scripts should be available in the source control)
>    * Mocks of 3th party systems
> * How to deploy to the different environments
>    * Urls, accounts used, etc
> * Tricky parts of the application
> * Conventions used in the project
>    * `Databse table names should be in plural`
>    * `Rest endpoints should be in plural and represent a resource`
>    * `The database scripts should be placed in folder <project root>/databse`

## Extract variable or a function when it makes the code more readable 
```diff
-if(context.getAttribute("TEST_FLAG")) {...}
+boolean isTestMode = context.getAttribute("TEST_FLAG");
+if(isTestMode) {...}
```
Stick to expressions when they could be read as a sentence
```java
if(type.startsWith("BASIC_")) {...}
```

`Eclipse - Ctrl + 1 -> Extract local variable`
`IntelliJ - Ctrl + Alt + V`

## Avoid Magic Numbers, Magic String, etc.
Use named constants, extract variabled or methods to express your intention
```java
// NOT OK
List<int[]> getCells() { 
   //0th element - status - 4 - FLAGGED
   //1th element - x
   //2nd element - y
   List<int[]> list1 = new ArrayList<int[]>();
   for (int[] x : theList) 
      if (x[0] == 4) list1.add(x); 
   return list1;
}
```

```java
// BETTER
List<int[]> getFlaggedCells() { 
   List<int[]> flaggedCells = new ArrayList<int[]>();
   for (int[] cell : gameBoard) {
      boolean isFlagged = cell[STATUS] == FLAGGED;
      if (isFlagged) 
         flaggedCells.add(cell); 
   }
   return flaggedCells;
}
```

```java
// BEST - extract class

class Cell {
   private CellStatus status;
   private int x;
   private int y;

   public Cell(int[] cellProperties) { //the bad domain design end here and will not spread to the rest of the application
      this.status = CellStatus.from(cellProperties[0]);
      this.x = cellProperties[1];
      this.y = cellProperties[2];
   }

   public boolean isFlagged() {
      return this.status == CellStatus.FLAGGED;
   }

   ...
}

List<Cell> getFlaggedCells() { 
   List<Cell> flaggedCells = new ArrayList<Cell>();
   for (Cell cell : gameBoard) {
      if (cell.isFlagged()) 
         flaggedCells.add(cell); 
   }
   return flaggedCells;
}
```
Bonus: You can go further and transform to a stream to get rid of the mutable variable flaggedCells.

```java
List<Cell> getFlaggedCells() { 
   return gameBoard.stream()
            .filter(cell -> cell.isFlagged())
            .collect(toList());
}
```

## Avoid abriviations, unless they are common and well known (HTTP, EGN, EIK, etc.)
## Avoid terms and metaphores that are hard for other to understand

## Be consistent with the naming

```java
interface CustomerRepository {
   long save(Customer customer);
   Optional<Customer> findById(long id);
}
```

```java
interface OrderRepository {
   long insert(Order order);
   Optional<Order> get(long id);
}
```

Both `save` and `insert` a good names but having them both in one project will make others wonder if they do the same thing.

## Class names
Single Responsibility Principle - SRP tells us that a class should have only one reason to change. The name of a class should explain this reason.

Avoid pre/postfixes that doesn't realy mean much:
* Helper
* Manager
* Util
* Info
* Data

What's the difference between `OrderInfo` and `OrderData`?
Instead: `DashboardSummaryOrderInfo` or `DataWarehouseOrderDetails`

## Branch names, commit mesages, PR descriptions, etc

Try to prefix names, mesages and descriptions with an issue identifier if the code change is related.

<pre>
git commit -m"<b>SOMEPROJECT-245</b> Support upsert operations in CRUD repository"
</pre>

## Don't get naming paralysis (or any paralysis in that matter). 
Yes, names are very important but they're not important enough to waste huge amounts of time on. If you can't think up a good name in X minutes, move on.

# Variables
## Minimize the scope of variables
* Global variables that are immutable/constants are OK.
* Avoid global variables that are written by one entity and read by many.
* AVOID global variables that mutated by multiple entities (e.g. `public static` variable that is not final ).

Declare variables as close to the place that they are used as possible.

## Avoid Accomulator variables

```java
class OrdersSummary {
   BigDecimal totalPrice;
   int toatalNumberOfOrders;
   List<String> allErrors;
   ...
}
```

```java
OrdersSummary calculateOrdersSummary(List<Order> orders) {
   BigDecimal totalPrice = BigDecimal.ZERO;              // for small methods it's not a Biggie, but for big methods it get very bad
   int toatalNumberOfOrders = 0;
   List<String> allErrors = new ArrayList<>();

   for (Order order : orders) {                          // this violates SRP -> separating this into 3 loops doin one thing is still better
      totalPrice = totalPrice.add(order.getPrice);
      toatalNumberOfOrders++;
      if(order.getDelivery().isFailed()) {
         allErrors.add(order.getDelivery().getFailReason());
      }
   }

   return new OrdersSummary(totalPrice, toatalNumberOfOrders, allErrors);
}
```

```java
OrdersSummary calculateOrdersSummary(List<Order> orders) {
   int toatalNumberOfOrders = orders.size();

   BigDecimal totalPrice = orders.stream()
                                 .map(order -> order.getPrice())
                                 .reduce(BigDecimal.ZERO, BigDecimal::add);

   List<String> allErrors = orders.stream()
                                  .filter(order -> order.getDelivery().isFailed())
                                  .map(order -> order.getDelivery().getFailReason())
                                  .collect(toList());

   return new OrdersSummary(totalPrice, toatalNumberOfOrders, allErrors);
}
```

## Choose the right type of variable
* For finite number of values use enums (e.g. Statuses)
* Don't use numberic types for variables that are not to be used in aritmetic operations

```java
String customerNumberString = "0123123";
int customerNumber = Integer.parseInt(customerNumberString);
System.out.println(customerNumber);// => 123123 
//you lose the 0. It's a wrong representation of the customer number even if the customer number is a number
```

* Maps are not for pairs of objects that does not represent key-value pairs a "bag" of objects - use objects or Tuples(but only of the scope is small)
```diff
-   Map<String, Object> people = new HashMap<>();
-   people.put("Michael", "Jordan");
-   people.put("Scottie ", "Pippen");
-   return people;
``` 

## Limit the state representation

The primitive types usually does not enforce the context.
Prefer types with more expressive values!

```java
class BranchCustomer {
   private String branchId; // can be "615", but also can be "Beer" or "zimbabwe"
   private String customerNumber;

   // other fields...
}
```

```java
class BranchCustomerNumber {
   private final String branchId;
   private final String customerNumber;

   public BranchCustomerNumber(String branchId, String customerNumber) {
      if(/*branchId is not 3 chars or the chars are not digits*/) throw new InvalidBranchCustomerNumberException(...);
      if(/*customerNumber is not 7 chars or the chars are not digits*/) throw new InvalidBranchCustomerNumberException(...);
   }
   //getters
}

class BranchCustomer extends Customer{
   private BranchCustomerNumber branchCustomerNumber;
}
```

```java
class RefreshPolicy { //has 4294967296 * 4294967296 = 1.8446744e+19 possible states
   int size;
   int frequncy; // 1 - dayly, 2 monthly, 3 yearly
}
```

```java
public enum RefreshSize {
    ON_WIFI(10),
    ON_NETWORK(1000);
 
    public final int size;
 
    private RefreshSize(int size) {
        this.size = size;
    }
}

public enum RefreshFrequency {
    DAYLY,
    MONTHLY,
    YEARLY;
}

class RefreshPolicy { // has 3 * 2 = 6 possible states, which is TESTABLE
   RefreshSize size;
   RefreshFrequency frequncy;
}
```

* Strings (a.k.a infinite number of possible values) are for data not for state  - use enums

```java
   order.setStatus("Active"); // Don't do that
```

```java
public static final String STATUS_ACTIVE = "Active"; // Better but still not ideal.  

// use STATUS_ACTIVE instead of "Active" in the code
order.setStatus(STATUS_ACTIVE);

order.setStatus("I still can set whatever string I want here");// ???
```

# Functions


## Small

***Function has the right size when you cannot extract anything more from it as a function.***

### Extract method
`Eclipse - Ctrl + 1 -> Extract Method`
`IntelliJ - Ctrl + Alt + M`

Extract method candidates:
* `if` - else blocks bigger than X lines
* loop blocks bigger than X lines
* `try` blocks bigger than X lines
* Lambdas with `{}`
```diff
-items.forEach(item -> {
-   ...
-   //20 lines of code
-   ...
-});

+items.forEach(item -> processItem(item));
```

Smaller methods run faster - it's more likely that the JIT compiler will optimize them over big methods

You should be able to explain what a function does in no more than 20 words without using words like “and” and “or”. 

## Do one thing and follow SRP

**FUNCTIONS SHOULD DO ONE THING. THEY SHOULD DO IT WELL.
THEY SHOULD DO IT ONLY.**

## One Level of Abstraction per Function

Don't mix different abstractions in one function:
* business logic
* data access
* error handling
* http/server stuff
* working with byte buffers, output/input streams, etc

```java
   public String someFunction() {
      StringBuilder message = new StringBuilder();
      Overdraft allowedOverdraft = client.calculateAllowedOverdraft();
      if(allowedOverdraft.getAmount() < MINIMUM_OVERDRAFT) {
         message.append("amount < MIN ")
      } 
      List<Manager> managers = jdbcTemplate.query("select * from Managers", new ManagerRowMapper());
      if(managers.isEmpty()) {
         message.append("no manager");
      }
      return message.toString();
   }
```

Naturally violates SRP!

## One Level of Indentation per Function
```java
public void myFunction(List<Person> people) {
   if(<some condition 1>) {
      for(Person person : people) {
         if(<some condition 2>) {
            doStuff(person);
         }
      }
   } else {
      doOtherStuff(people);
   }
}
```
Do this instead:
```java
public void myFunction(List<Person> people) {
   if(<some condition 1>) {
      handleSomeCondition1(people);
   } else {
      doOtherStuff(people);
   }
}
```

## Reading Code from Top to Bottom: The Stepdown Rule (Extract method when it makes the code more readable)
```java
if (null != response && response.getAdvice() != null && response.getAdvice().getStatus().equals(AdviceStatusEnum.ERROR)) {
      responseEntity = new ResponseEntity<>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
} else {
      responseEntity = new ResponseEntity<>(response, headers, HttpStatus.OK);
}
```

```java
if (isStatusError(response)) {
      responseEntity = new ResponseEntity<>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
} else {
      responseEntity = new ResponseEntity<>(response, headers, HttpStatus.OK);
}

...

boolean isStatusError(Response response) {
   return null != response && response.getAdvice() != null && response.getAdvice().getStatus().equals(AdviceStatusEnum.ERROR);
}
```


## Function Arguments

### Avoid output argumnets and in general avoid modifying arguments

Don't mutate the arguments

* Arguments -> for input 
* Returning object -> for output

`f(x) = y`

### Try to keep the arguments on the same level of abstraction as the function
```java
Overdraft calculateOverdraftLimitForClient(Client client, ByteArraysOutputStram transactionsFile); //not OK
```
### Avoid boolean parameters
* makes the call hard to read 
```java
activateUser(user, true); //what does true mean here??`
```
* function does more than one thing and violates SPR 
* often happens to big old functions that have to change 

### Consider wrapping some of the function arguments in a class when appropriate
```diff
-Circle makeCircle(double x, double y, double radius);
+Circle makeCircle(Point center, double radius);
```
Always search for the occurance of the same params that are extracted, to be able to replace them with the new class.
The logic related to this parameters will move to the new class.

### Check the input parameters - fail as fast as possible
```java
BigDecimal devide(Integer divident, Integer divisor) {
   Objects.requireNonNull(divident);
   Objects.requireNonNull(divisor);
   ...
}
```
`#LimitTheStateRepresentation`

### Clearly define the interface of the function
```diff
-void checkPersonIdentificationNumber(Person person) { //to test the function a whole person is needed
-   String identNumber = person.getIdentNumber(); //person has a lot more fields
-   ...
-}
+void checkPersonIdentificationNumber(String identNumber) {
+   ...
+}
```

## Return types

```void``` is suspicious.

Any method returning void is either meaningless or operates through side-effects, such as writing to display, network, file or database

### Don't return null
Use Optional to express that the function can return null value

```java
Optional<Person> findByName(String name) {
   ...
}
```

### Throw errors, don't return them

* Functions either return the value that they are mean to produce or throw an error 
* void functions either complete successfully or throw an error
* Don't return NULL to "express" that something went wrong.
* Don't return a "message" to tell if the function has completed successfully or something went wrong.

```java
Integer findPersonAge(...) {
   try {
      ...
      return person.getAge();
   } catch(Exception e) {
      return null;
      //return -1;
   }
}
```

## Refactoring Demo =>

# Side Effects

* Read/Write to the environment
* Database access
* WebService calls
* File system access 

Side effects have their own level of abstraction.

## Extract environment (Date-time, properties, current OS)

Having a side effect in the method makes it hard/impossible to test
```java
int getDaysToNewYear() {
   LocalDate today = LocalDate.now();
   LocalDate newYearsDay = LocalDate.of(today.getYear(), Month.DECEMBER, 31);
   return Period.between(today, newYearsDay).getDays();
}
```
```java
@Test
public void getDaysToNewYear_shouldReturn2_for29December() {
   //Good luck testing getDaysToNewYear()
}
```

```java
int getDaysToNewYear(LocalDate today) {
   LocalDate newYearsDay = LocalDate.of(today.getYear(), Month.DECEMBER, 31);
   return Period.between(today, newYearsDay).getDays();
}
```

```java
@Test
public void getDaysToNewYear_shouldReturn2_for29December() {
   assertEquals(2, getDaysToNewYear(LocalDate.of(2019, Month.DECEMBER, 29)));
}
```

## Pure functions

1. The function does not chnage anything
2. The function does not depend on anything that can change

```java
int doubleIt(int number) {
   return number * 2;
}
```

## Separate side effects from business logic

You get on the train

&darr;
   
The train goes from A to B

&darr;

You get off the train

 [side effect] Get all of the input from database, rest service, etc.

&darr;
   
 [pure functions] Do all of the business logic, calculations, etc.

&darr;

 [side effect] Persists in the database, file, etc.

Try to split functions to one pure function and up to two(input and output) impure functions. 

```java
class OrdersService {
   ...
   
   void processOrder(Long orderId) {
      Order order = this.ordersRepository.getById(orderId);                                                 //side effect (input) no business logic allowed
      ItemsAvailablility itemsAvailablility = this.inventoryGateway.checkAvailability(order.getItems());    //side effect (input) no business logic allowed

      ProcessedOrder processedOrder = order.process(itemsAvailablility);                                    // pure function - business logic

      this.ordersRepository.save(processedOrder);                                                           //side effect (output) no business logic allowed
      this.auditLogRepository.log(processedOrder);                                                          //side effect (output) no business logic allowed
   }
}
```

* The code is easy to reason about
* The Functional Core is easy to test
* It's easier to make IO side effects parallel and improve performance

# Error Handling

## Don't ignore exceptions! In the rear cases when this is needed - add a comment.
"Something does not work and we don't know why" - situation.

```diff
try {
   ...
} catch(Exception e) {
-   e.printStackTrace(); // NEVER
-   logger.error("Error in some function " + e.getMessage()); // Will not provide enough context. e.g. "Error in some function NullPointerException"
+   logger.error("Error in some function ", e);
}
```
## At least log them. 

## Use Exceptions Rather Than Return Codes
```diff
-class Result<T> {
-   private T result;
-   private Status status; // SUCCESS || ERROR
-
-   ...
-}

-try {
-   ...
-   return new Result(functionResult, SUCCESS);
-} catch(Exception e) {
-   return return new Result(null, ERROR);
-}

```

## Catch or Pass 
### Catch exceptions only if you know what to do with them. Else let them "buble up".

```java
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
 
    @ExceptionHandler(value ={IllegalArgumentException.class, IllegalStateException.class })
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = "This should be application specific";
        return handleExceptionInternal(ex, bodyOfResponse, 
          new HttpHeaders(), HttpStatus.CONFLICT, request);
    }
}
```

## Use Unchecked/Runtime Exceptions
### Checked Exception violate the Open/Closed Principle
If you throw a checked exception from a method in your code and the catch is three levels
above, you must declare that exception in the signature of each method between you and
the catch. This means that a change at a low level of the software can force signature
changes on many higher levels.

### Checked exceptions breaks encapsulation
All functions in the path of a throw must know about details of that low-level exception.

### Checked exceptions does not rollback declaratve transactions!

## Provide Context with Exceptions
```java
public class PersonNotFoundException extends RuntimeException {
   private Long searchId; 
   public PersonNotFoundException(Long searchId, String message) {
      this.searchId = searchId;
      super(message);
   }
   //getters...
}
```

## Don't collect the stacktrace for business exceptions
```java
Throwable(String message, Throwable cause, boolean enableSuppression,boolean writableStackTrace)
```
```java
public class SuppressableStacktraceException extends Exception {
    private boolean suppressStacktrace = false;

    public SuppressableStacktraceException(String message, boolean suppressStacktrace) {
        super(message, null, suppressStacktrace, !suppressStacktrace);
        this.suppressStacktrace = suppressStacktrace;
    }

    @Override
    public String toString() {
        if (suppressStacktrace) {
            return getLocalizedMessage();
        } else {
            return super.toString();
        }
    }
}
```

# Objects

## Keep logic in it's domain

If a particular variable/field has a behavior related to it - extract class.

```java
String url = ...

boolean isSecure(String url) {
   return "https".equals(url.substring(0, url.indexOf("://")));
}

```

```java
class Url {
   private final String url;
   Url(String url) {
      Objects.requireNonNull(url);//also check if not empty, etc.
   }

   boolean isSecure() {
      return "https".equals(this.url.substring(0, url.indexOf("://")));
   }

   //all other methods related to url will coalesce in this class and not "float" trough various other classes
}
```

`#LimitTheStateRepresentation`

## Prefer OOP over procedural code

Procedural code
```java
class OrderService {

   public void addProductToOrder(Order order, Product product, int amount) {
      Optional<OrderItem> item = order.getItems().stream()
         filter(item -> item.getId().equals(product.getId()))
         findFirst();

      if(item.isPresent()) { // if this or similar logic is needed somewhere else - it will have to be duplicated
         item.get().increaseQuantityBy(amount);  
      } else {
         order.getItems().add(new OrderItem(product, amount));
      }
   }
}
```

OOP
```java
class Order {
   private List<OrderItem> items;
   ...

   public void addProduct(Product product, int amount) { // encapsulate the Order class, so this is the only way to add product
      Optional<OrderItem> item = items.stream()
         filter(item -> item.getId().equals(product.getId()))
         findFirst();

      if(item.isPresent()) {
         item.get().increaseQuantityBy(amount);  
      } else {
         order.getItems().add(new OrderItem(product, amount));
      }
   }
}
```

## Be as restrictive as possible. Loosen up the restrictions only if needed. e.g.
* Make all variables and class properties final untill you need them to be mutable 
* don't add setters in a class untill you need it
* don't add getters in a class untill you need it
```diff
//we need the number of items cheaper than 10€ in an order
-class Order {
-   ...
-   private List<OrderItem> items;
-
-   public List<OrderItem> getItems() {
-      return items;
-   }
-}
-
-// is some other class
-order.getItems().stream()
-   .filter(item -> item.getPrice().compareTo(BigDecimal.TEN) < 0)
-   .count();

+class Order {
+   ...
+   private List<OrderItem> items;
+
+   public List<OrderItem> getNumberOfCheapItems() {
+      return this.items.stream()
+     .filter(item -> item.getPrice().compareTo(BigDecimal.TEN) < 0)
+     .count();
+   }
+}
```

## Avoid multiple variables/fields depending on each other(Data Clumps)
Extract Data clumps as classes

Can one be used (makes sense) without the other?

```java
class DataExport {
   ...
   LocalDateTime start;
   LocalDateTime end;
   ...
}
```

```java
class Range {
   private final LocalDateTime start;
   private final LocalDateTime end;

   public Range(LocalDateTime start, LocalDateTime end) {
      // validate not null, start before end, etc.
   }
}

class DataExport {
   ...
   Range range;
   ...
}
```

## Prefer computation over duplicated data

```java
class Account {
   Status status; // [Active, Inactive, Locked]
   List<Lock> locks; 
   // getter setter
}
```

```java
//setting a lock
account.getLocks().add(new Lock("Case 123"));
account.setStatus(Locked);
```


```java
//removing a lock
account.getLocks().remove(lockToRemove);
if(accounts.getLocks().isEmpty()) {
   account.setStatus(Active);
}
```

Instead

```java
class Account {
   Status status; // [Active, Inactive, Locked] - in the state of the object the status is either Active or Inactive, but never Locked
   List<Lock> locks; 
   // No getter setter, addLock(Lock), removeLock(Lock)

   Status getStatus() {
      if(!this.locks.isEmpty()) return Status.Locked; // The locked status us computed from the list of locks
      return this.status;
   }
}
```


## Objects vs Data structures
*Objects* hide their data behind abstractions and expose functions that operate on that data. 
*Data structures* expose their data and have no meaningful functions.

## Discover value objects
```java
privateMethod(a, b); 
//-> 
MyUtils.method(a, b); 
//-> 
new MyValueObject(a, b).method()
```

## Make class propertis either nullable or not 
```java
class Person {
   private final String name;
   private final String address;

   public Person(String name, String address) {
      Objects.requireNonNull(name);
      this.name = name;
      this.address = address;
   }

   public String getName() { // is never null can return it as is
      return this.name;
   }

   public Optional<String> getAddress() { // can be null - force the users of the class to check it
      return Optional.ofNullable(this.address)
   }
}
```

## Prefer factory methods when multiple constructors are needed
```java
class Person {
   private final String name;
   private Person(String name) {
      this.name = name;
   }

   public static Person of(String name) {
      return new Person(name);
   }
}
```
* Unlike constructors, factory methods have names.
* Factory methods are not required to create a new object each time they’re invoked 
   * caching
* Factory methods can return an object of any subtype of their return type
* The class of the returned object, from factory method, can vary from call to call as a function of the input parameters
   * `EnumSet` factory methods return `RegularEnumSet` (backed by a single long) if the set has less than 64 elements
   * `EnumSet` factory methods return `JumboEnumSet` (backed by an array of long) if the set has more than 64 elements

## Mutability is the new GOTO
Mutability should be avoided or "pushed" to lower level.

## Minimize mutability
Classes should be immutable unless there’s a very good reason to make them mutable.

e.g. `String`, `BigInteger` and `BigDecimal`

1. Don’t provide methods that modify the object’s state (known as mutators).
2. Ensure that the class can’t be extended. e.g. making the class final
3. Make all fields final. This clearly expresses your intent in a manner that is enforced by the system. 
4. Make all fields private. This prevents clients from obtaining access to mutable objects referred to by fields and modifying these objects directly.
5. Ensure exclusive access to any mutable components. If your class has any fields that refer to mutable objects, ensure that clients of the class cannot obtain references to these objects.

```java
public final class Complex {
   private final double re;
   private final double im;

   public Complex(double re, double im) {
      this.re = re;
      this.im = im;
   }
   public double realPart() { return re; }
   public double imaginaryPart() { return im; }
   public Complex plus(Complex c) { return new Complex(re + c.re, im + c.im); }
   public Complex minus(Complex c) { return new Complex(re - c.re, im - c.im); }
   public Complex times(Complex c) { return new Complex(re * c.re - im * c.im, re * c.im + im * c.re); }
   public Complex dividedBy(Complex c) { 
      double tmp = c.re * c.re + c.im * c.im;
      return new Complex((re * c.re + im * c.im) /tmp, (im * c.re - re * c.im) / tmp);
   }
   @Override public boolean equals(Object o) {
   ...
   }
   @Override public int hashCode() {
   ...
   }
   @Override public String toString() {
   ...
   }
}
```

## Reuse common immutable objects (use static factories to cache)

```java
public static final Complex ZERO = new Complex(0, 0);
public static final Complex ONE = new Complex(1, 0);
public static final Complex I = new Complex(0, 1);
```

## Immutable objects are:
* Easier to reason about - NO OBJECTS IN IVALID STATE
* Easier to share/cache
* Thread-safe - no synchronisation needed
* Good Map keys and Set elements, since these typically do not change once created (also the hash method can be cached or precomputed for better performance)

## Immutable Order Example
```java
public abstract class Order {
    protected final LocalDateTime orderDate;
    protected final List<OrderItem> items;
    protected final String client;

    private Order(LocalDateTime orderDate, List<OrderItem> items, String client) {
        Objects.requireNonNull(orderDate);
        Objects.requireNonNull(items);
        Objects.requireNonNull(client);
        this.orderDate = orderDate;
        this.items = items;
        this.client = client;
    }
    
}

class NewOrder extends Order {
    public NewOrder(LocalDateTime orderDate, List<OrderItem> items, String client) {
        super(orderDate, items, client);
    }

    public void addItem(OrderItem item) {
        // all kind of business validation can be added here
        // logic related to adding of item can be added here - e.g. if the item already exists - increase the count
        this.items.add(item);
    }

    public void removeItem(String item) {
        this.items.remove(item);
    }

    public ProcessedOrder process(LocalDateTime sentToCourierAt, String courierName) {
        if(this.items.isEmpty()) { // no need to a null check here, because items is checked in the constructor and cannot be changed
            throw new NoOrderItemsException("Order without items cannot be processed.");
        }
        return new ProcessedOrder(this.orderDate, this.items, this.client, sentToCourierAt, courierName);
    }

    public CanceledOrder cancel(LocalDateTime canceledOn, String cancelReason) {
        return new CanceledOrder(this.orderDate, this.items, this.client, canceledOn, cancelReason);
    }
}

class ProcessedOrder extends NewOrder {
    protected final LocalDateTime sentToCourierAt; // should be extracted as a class
    protected final String courierName;

    public ProcessedOrder(LocalDateTime orderDate, List<OrderItem> items, String client, LocalDateTime sentToCourierAt, String courierName) {
        super(orderDate, items, client);
        Objects.requireNonNull(courierName);
        Objects.requireNonNull(sentToCourierAt);
        this.sentToCourierAt = sentToCourierAt;
        this.courierName = courierName;
    }

    @Override
    public void addItem(OrderItem item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeItem(OrderItem item) {
        throw new UnsupportedOperationException();
    }

    public DeliveredOrder deliver(LocalDateTime deliveredOn) {
        return new DeliveredOrder(this.orderDate, this.items, this.client, this.sentToCourierAt, this.courierName, deliveredOn);
    }
}

class CanceledOrder extends NewOrder {
    private final LocalDateTime canceledOn;
    private final String cancelReason;

    public CanceledOrder(LocalDateTime orderDate, List<OrderItem> items, String client, LocalDateTime canceledOn, String cancelReason) {
        super(orderDate, items, client);
        Objects.requireNonNull(canceledOn);
        Objects.requireNonNull(cancelReason);
        this.canceledOn = canceledOn;
        this.cancelReason = cancelReason;
    }

    @Override
    public void addItem(OrderItem item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeItem(OrderItem item) {
        throw new UnsupportedOperationException();
    }
}

class DeliveredOrder extends ProcessedOrder {
    private final LocalDateTime deliveredOn;

    public DeliveredOrder(LocalDateTime orderDate, List<OrderItem> items, String client, LocalDateTime sentToCourierAt, String courierName, LocalDateTime deliveredOn) {
        super(orderDate, items, client, sentToCourierAt, courierName);
        Objects.requireNonNull(deliveredOn);
        this.deliveredOn = deliveredOn;
    }

    @Override
    public void addItem(OrderItem item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeItem(OrderItem item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "DeliveredOrder{" +
                "orderDate=" + orderDate +
                ", items=" + items +
                ", client='" + client + '\'' +
                ", sentToCourierAt=" + sentToCourierAt +
                ", courierName='" + courierName + '\'' +
                ", deliveredOn=" + deliveredOn +
                '}';
    }
}

class NoOrderItemsException extends RuntimeException {
    public NoOrderItemsException(String message) {
        super(message);
    }
}

public class Main {
    public static void main(String[] args) {
        NewOrder order = new NewOrder(LocalDateTime.now(), new ArrayList<>(), "some client");
        order.addItem(new OrderItem("some item"));
        ProcessedOrder processedOrder = order.process(LocalDateTime.now(), "courier");
        DeliveredOrder deliveredOrder = processedOrder.deliver(LocalDateTime.now());
        System.out.println(deliveredOrder);
    }
}
```


# Law of Demeter
A module should not know about the innards of the objects it manipulates.
A method f of a class C should only call the methods of these:
* C
* An object created by f
* An object passed as an argument to f
* An object held in an instance variable of C

The method should not invoke methods on objects that are returned by any of the
allowed functions. In other words, talk to friends, not to strangers.

OK
```java
public class LawOfDemeter {
   private Topping cheeseTopping;

   public void doSomethingWithPizza(Pizza pizza) {
      // (1) it's okay to call our own methods
      doSomething();
      
      // (2) it's okay to call methods on objects passed in to our method
      int price = pizza.getPrice();
      
      // (3) it's okay to call methods on any objects we create
      float weight = cheeseTopping.getWeightUsed();
      
      // (4) any directly held component objects
      Foo foo = new Foo();
      foo.doBar();
   }

   private void doSomething()
   {
      // do something here ...
   }
}
```

NOT OK
```java
public class LawOfDemeter {
   public boolean isInRiskArea(Person person) {
      String neighborhood = person.getAddress().getNeighborhood(); 
      if(riskNeighborhoods.contains(neighborhood)) {
         ...
      }
   }
}
```

# API/Module design

## A module, class or function should work correctly by itself and should not depend on the result some other module, class, function or variable

This will make `someFunction` unusable in other parts of the code. `someFunction` is tightly couple to the piece of code that provides `order`
```java
//NOT OK
void someFunction(Order order) {  
   order.getItems().get(0); //We know that at this point the order have only one item... 
   ... 
}
```

## The structure of the project should express the meaning of the project

This doesent tell much about the project itself. It's pretty generic.
* src/main/java
   * models
   * repositories
   * services
   * views

This expresses the meaning of the project - it's an application that manages *orders*, made by *customers*, which can also *pay* for their orders and all this can be summarized by a bunch of *reports*. 
* src/main/java
   * customers
   * orders
   * payments
   * reporting

## Make each class or member as inaccessible as possible
Use package-private  as much as possible
Expose only the functionality that has to be exposed

* src/main/java
   * authentication
      * public Authentication.class
         * public factoryMethod: ldap(String ldapUrl)
         * public factoryMethod: database(String connectionString)
         * public method: authenticate(username)
      * package private LdapAuthentication.class
      * package privtae DatabaseAuthentication.class
      * package private AuthenticationAuditLogger.class

## The I in SOLID
Interfaces

```java
// NOT OK
interface PersonSerivce {
   // all methods of PersonSerivce - findById, save, etc
}

class PersonSerivceImpl implements PersonSerivce {
   // implementations of all methods of PersonSerivce - findById, save, etc
}
```

If a class has a dependency that has a couple of methods but only 1-2 are used - conside defineing an interface that has only this 1-2 methods.

`#ClearlyDefineTheInterfaceOfAFunction`

```java
class OrderService {
   private PersonService personService; //the interface PersonService should not contain methods that are not used by OrderService

   void sendOrderToClient(long clientId) {
      this.personService.findById(clientId); //nothig else from personService is used in this class
      ...
   }
}
```
Multi interface implementation and inheritance is  thing in Java.

```java
public interface PersonReader {
   public Person findById(Long id);
}

public interface PersonPersister {
   public void save(Person person);
}

public interface PersonService extends PersonReader, PersonPersister {
   
}
```

```java
class OrderService {
   private PersonReader personReader; //has only the required methods - findById

   void sendOrderToClient(long clientId) {
      this.personReader.findById(clientId);
      ...
   }
}
```

# Code smells
* https://www.youtube.com/watch?v=D4auWwMsEnY
* Smells to Refactorings - https://www.industriallogic.com/wp-content/uploads/2005/09/smellstorefactorings.pdf

## Bloaters
Bloaters are code, methods and classes that have increased to such gargantuan proportions that they are hard to work with. Usually these smells do not crop up right away, rather they accumulate over time as the program evolves (and especially when nobody makes an effort to eradicate them).
1. Long Method
2. Large Class
3. **Primitive Obsession**
4. Long Parameter List
5. **Data Clumps**

### Primitive Obsession
Passing around objects that are too dumm (primitive) and peaces of code decide what to do with them based on some context that is not enforced. 

```java
String carRegistrationNumber;
```

```java
class CarRegistrationNumber {
   private String cityIdentifier; // e.g. CB
   private String number; // 1234
   private String pstfixCode; // KM

   public CarRegistrationNumber(String carRegistrationNumber) {
      // parse the parameter carRegistrationNumber and initialize the class fields
      // throw exception if the parameter is not a valid car registration number
   }

   // put all logic that is related to that number in this class
}
```

`#LimitTheStateRepresentation`
`#ProceduralProgramingVsOOP`

### Data Clumps
Two or more peaces of data that appear together all the time.

Refactoring => Extract the data peaces in their own object.
A good thest is: "If one of the data values is deleted does the other make any sense?".

```java
class SomeRepository {
   List<Sale> getTotalSales(LocalDateTime from, LocalDateTime to) {
      return from(Sales)
      .where(from <= saleTime && saleTime <= to)
      .fetch();
   }

   List<Sale> getWeeklySales(LocalDateTime from) {
      LocalDateTime to = from.plusDays(7);
      return from(Sales)
      .where(from <= saleTime && saleTime <= to)
      .fetch();
   }

   List<Sale> getTotalExpense(LocalDateTime from, LocalDateTime to) {
      LocalDateTime fromOrWeekBefore = from != null ? from : LocalDateTime.now().plusDays(-7);
      LocalDateTime toOrNow = to != null ? to : LocalDateTime.now();
      return from(Expense)
      .where(fromOrWeekBefore <= saleTime && saleTime <= toOrNow)
      .fetch();
   }
}
```

```java
class DateRange {
   private LocalDateTime from; 
   private LocalDateTime to;

   public DateRange() {
      this(LocalDateTime.now().plusDays(-7), LocalDateTime.now());
   }

   public DateRange(LocalDateTime from) {
      this(from, LocalDateTime.now())
   }

   public DateRange(LocalDateTime from, LocalDateTime to) {
      this.from = from != null ? from : LocalDateTime.now().plusDays(-7);
      this.to = to != null ? to : LocalDateTime.now();      
      if(this.from.isAfter(this.to)) throw new IllegalArgumentException("From cannot be after to!");
   }

   public boolean contains(LocalDateTime date) {
      //...
   }
   //getters, NO setters, toString(), etc.
}
```

```java
class SomeRepository {
   List<Sale> getTotalSales(LocalDateTime from, LocalDateTime to) {
      return from(Sales)
      .where(new Daterange(from, to).contains(saleTime))
      .fetch();
   }

   List<Sale> getWeeklySales(LocalDateTime from) {
      return from(Sales)
      .where(new Daterange(from).contains(saleTime))
      .fetch();
   }

   List<Sale> getTotalExpense(LocalDateTime from, LocalDateTime to) {
      return from(Expense)
      .where(new Daterange(from, to).contains(saleTime))
      .fetch();
   }
}
```
Resolving Data Clumps "moves" the behavior into the extracted class and makes it resable, when you chnage it it chnages everywhere, etc. 
`#KeepLogicInTheDomain` 

## Object-Orientation Abusers
All these smells are incomplete or incorrect application of object-oriented programming principles.
1. **Switch Statements**
2. **Temporary Field**
3. Refused Bequest
4. Alternative Classes with Different Interfaces

### Switch Statements
```java
class Bird {
  // ...
  double getSpeed() {
    switch (type) {
      case EUROPEAN:
        return getBaseSpeed();
      case AFRICAN:
        return getBaseSpeed() - getLoadFactor() * numberOfCoconuts;
      case NORWEGIAN_BLUE:
        return (isNailed) ? 0 : getBaseSpeed(voltage);
    }
    throw new RuntimeException("Should be unreachable");
  }
}
```

```java
abstract class Bird {
  // ...
  abstract double getSpeed();
}

class European extends Bird {
  double getSpeed() {
    return getBaseSpeed();
  }
}
class African extends Bird {
  double getSpeed() {
    return getBaseSpeed() - getLoadFactor() * numberOfCoconuts;
  }
}
class NorwegianBlue extends Bird {
  double getSpeed() {
    return (isNailed) ? 0 : getBaseSpeed(voltage);
  }
}

// Somewhere in client code
speed = bird.getSpeed();
```

### Temporary Field
Temporary fields get their values (and thus are needed by objects) only under certain circumstances. Outside of these circumstances, they’re empty.
`#OrderHierarchiExample`
`#ExtractClass` `#ReplaceMethodWithMethodObject`

## Change Preventers
These smells mean that if you need to change something in one place in your code, you have to make many changes in other places too. Program development becomes much more complicated and expensive as a result.
1. Divergent Change
2. **Shotgun Surgery**
3. Parallel Inheritance Hierarchies

### Shotgun Surgery
Making any modifications requires that you make many small changes to many different classes.

`#MoveMethod` `#MoveField` `#InlineClass` `#KeepLogicInTheDomain` 


## Dispensables
A dispensable is something pointless and unneeded whose absence would make the code cleaner, more efficient and easier to understand.
1. Comments
2. Duplicate Code
3. Lazy Class
4. **Data Class**
5. Dead Code
6. **Speculative Generality**

### Data Class
These are simply containers for data used by other classes. These classes do not contain any additional functionality and cannot independently operate on the data that they own.

```java
class Order {
   private LocalDateTime orderDate;
   Private List<OrderItem> items;

   // getters, setters
   // nothing else
}
```
`#KeepLogicInTheDomain`

### Speculative Generality
A code that is written because of feature that might arrive in the future.

* We're bad guessers!
* It makes the code hard to reason about. Code is read more often than it's written.
* Increases abstraction.

```java
class TheWorld {
   private List<ElvisPresly> elvises; //in case he gets colonned one day
}
```
This leads to:
```java
elvises.get(0) //we know he is the one and only for now
```

> **Premature Optimization Is the Root of All Evil**
>
> A code that is written in a certain way because it will be more performant. 
> 
> Source of bad performance:
> * IO operations are slower than memory and CPU by a factor of millions
> * Objects that are meant to be created once
>    * Pools (Connection pools, Thread pools)
>    * Factories 
>    * Jackson ObjectMapper
> * Regexes might be slow
> 
> Don't do something just because you think it's slow. The JVM optimizes a lot under the hood
> Iterating a List of 100 elements is super fast, dont need to worry about it!
> 
> **Write code that works (and is readable, testable and maintainable) then use profilers and Load performance tests to find out where the problem is.**
> * VisualVM
> * FlightRecorder - [link](https://www.baeldung.com/java-flight-recorder-monitoring)
> * Gatling - [link](https://gatling.io/)
 

## Couplers
All the smells in this group contribute to excessive coupling between classes or show what happens if coupling is replaced by excessive delegation.
1. Feature Envy
2. Inappropriate Intimacy
3. **Message Chains** - `#LawOfDemeter`
4. Middle Man
5. Incomplete Library Class

# Sources

1. Clean Code by Uncle Bob Martin
2. https://sourcemaking.com/refactoring/smells
3. https://refactoring.guru/
4. https://itnext.io/solid-principles-explanation-and-examples-715b975dcad4