# <center>How to fix working code</center>
<center>Evgeni Dimitrov</center>


# The author's names task

Get the unique surnames in uppercase of the first 15 book authors that are 50 years old or older.

```
{
   books: [
      {
         bookName: "Doesn't really matter"
         author: {
            age: 45,
            surname: "SomeSurname"
         }
      },
      ...
   ]
}
```

# The author's names task
```java
List<Author> authors = new ArrayList<>();

for (Book book : books) {
   Author author = book.getAuthor(); 
   if(author.getAge() > 50){
         authors.add(author);
         if(authors.size() > 15) 
            break;
   }
}

List<String> result = new ArrayList<>();
for (Author author : authors) {
   String name = author.getSurname().toUpperCase();
   if(result.contains(name)) {
         result.add(name);
   }
}
```

# The author's names task
```java
List<String> authrNames = books.stream()
                .map(book -> book.getAuthor())
                .filter(author -> author.getAge() > 50)
                .limit(15)
                .map(Author::getSurname)
                .map(String::toUpperCase)
                .distinct()
                .collect(toList()); 
```

# The author's names task
```java
List<String> authrNames = books.stream()
                .map(book -> book.getAuthor())
                .filter(author -> author.getAge() > 50)
                .distinct()                                // <=
                .limit(15)
                .map(Author::getSurname)
                .map(String::toUpperCase)
                .distinct()
                .collect(toList()); 
```

# The author's names task
```java
List<Author> authors = new ArrayList<>();

for (Book book : books) {
   Author author = book.getAuthor();
   if(author.getAge() > 50 && !authors.contains(author)){         // <=
         authors.add(author);
         if(authors.size() > 15) 
            break;
   }
}

List<String> result = new ArrayList<>();
for (Author author : authors) {
   String name = author.getSurname().toUpperCase();
   if(result.contains(name)) {
         result.add(name);
   }
}
```

# Overview
## Questions to answer
* Is code easy to read?
* Is code easy to unit test? Code that is easy to test is easy to reason about.
* Is code easy to reason?
* Is application easy to deploy?
* Is it easy to find bugs and chnage functionality?

# Overview

## The start defines the end

If the foundation is weak the rest cannot be solid.
```
   *********
    ***
      **
      *
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

1. Right after the code is working and the unit tests are done
2. In the scope of a rlatevly big chnage
3. Before the testing has started

# Sonarqube
https://www.sonarqube.org/

* For the IDE
* As a server application

# Names

## True or False ?

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

## Use Searchable Names

* Prefer long descriptive names over abbreviations
```java
HasThisTypePatternTriedToSneakInSomeGenericOrParameterizedTypePatternMatchingStuffAnywhereVisitor
```
* Short descriptive names are still the best
* Prefer pronounceable names

```diff
- private long elpsTime;
+ private long elapsedTimeInMilliseconds;
```

## Avoid Mental Mapping and "encription"

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
+int binarySearch(int arr[], int leftIndex, int rightIndex, int +keyToFind) {
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

## Extract variable when it makes the code more readable 
```diff
-if(context.getAttribute("TEST_MODE")) {...}
+boolean isTestMode = context.getAttribute("TEST_MODE");
+if(isTestMode) {...}
```
Stick to expressions when they could be read as a sentence
```java
if(type.startsWith("BASIC_")) {...}
```

## Avoid member prefixes
Avoid prefixing member variables with “m_” . Your classes and functions should be small enough that you don’t need prefixes.

## Avoid abriviations, unless they are common and well known (HTTP, EGN, EIK, etc.)
## Avoid terms and metaphores that are hard for other to understand

## Class names
Class name should be noun
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
git commit -m"<b>DATAJPA-245</b> Support upsert operations in CRUD repository"
</pre>

## Don't get naming paralysis (or any paralysis in that matter). 
Yes, names are very important but they're not important enough to waste huge amounts of time on. If you can't think up a good name in 10 minutes, move on.

# HIDDEN CHAPTER - Generl skills
## Naming to general skills mapping:
* Consistent
* Organized
* Self management
* Responisbility
* Communicate it with others
* Tell the TRUTH

## Take Responsibility
Take responsibility for your mistakes and try to fix them. 
Don't make excuses! Don't blame someone else!  Propose solutions instead.
"Just ask questions and remember that nobody knows what he/she is doing" - You're not responsible for not knowing something at a given point of time, but you have to take action to learn that thing.
Before asking a question:
* Think about all resources that you have access to. Can you find the answer there?
* Prepare your question - provide enough context. Stackoverflow is good way to learn to ask questions - "Off topic", "Too broad", "What have you tried?"

### Pick your poison - do something and make a mistake or do nothing and make a mistake.

## Don't Live with Broken Windows

# Variables
## Minimize the scope of variables
Global variables that are immutable/constants are OK.
Avoid global variables that are written by one entity and read by many.
AVOID global variables that mutated by multiple entities (e.g. `public static` variable that is not final ).

Declare variables as close to the place that they are used as possible.


# Variables

# State

## True or False

A value without context does not really matter. That's why it's important to name variables and use the right data structures.

## Limit the state representation

The primitive types usually does not enforse the context.
Prefer types with more expressive values!

```java
abstract class Customer {
   private String phone;
}

class BranchCustomer extends Customer{
   private String branchId;
   private String customerNumber;
}

class PotentialClient extends Customer{
   
}
```

```java
class BranchCustomer extends Customer{
   private String branchId; // can be "615", but also can be "Beer" or "zimbabwe"
   private String customerNumber;
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

* Maps are not for pairs of objects that does not represent key-value pairs a "bag" of objects - use objects or Tuples(but only of the scope is small)
```java
   Map<String, Object> person = new HashMap<>();
   person.put("firstName", "Michael");
   person.put("lastName", "Jordan");
   return person;
```

## Be as restrictive as possible. Loosen up the restrictions only if needed. e.g.
* Make all variables and class properties constants 
* don't add setters in a class untill you need it
* don't add getters in a class untill you need it
```diff
//we need the number of items cheaper than 10€ in an order
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
OrdersSummary clculateOrdersSummary(List<Order> orders) {
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
OrdersSummary clculateOrdersSummary(List<Order> orders) {
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


# Functions


## Small

~~Function should fit on the screen.~~

***Function has the right size when you cannot extract anything more from it as a function.***

### Extract method
Extract method candidates:
* if - else blocks bigger than X lines
* loop blocks bigger than X lines
* try blocks bigger than X lines

Smaller methods run faster - it's more likely that the JIT compiler will optimize them over big methods

You should be able to explain what a function does in no more than 20 words without using words like “and” and “or”. 

Don't use a `{}` for lambdas!


## Do one thing

**FUNCTIONS SHOULD DO ONE THING. THEY SHOULD DO IT WELL.
THEY SHOULD DO IT ONLY.**

## One Level of Abstraction per Function

Don't mix different abstractions in one function:
* business logic
* data access
* string concatenation
* http/server stuff

```java
   public String someFunction() {
      StringBuilder message = new StringBuilder();
      Overdraft allowedOverdraft = client.calculateAllowedOverdraft();
      if(allowedOverdraft.getAmount() < MINIMUM_OVERDRAFT) {
         message.append("amount < MIN ")
      } 
      Manager manager = jdbcTemplate.query("select * from Managers", new ManagerRowMapper());
      if(whatever == null) {
         message.append("no manager");
      }
      return message.toString();
   }
```

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

## Refactoring Demo =>

## Prefer clean code over clever code.
Prefer code that expresses the desired behavior over code that shorter/hacky.
Prefer cean code over performant code, if it's not IO intensive.

## Reading Code from Top to Bottom: The Stepdown Rule
```java
if (null != response && response.getAdvice() != null && !response.getAdvice().getStatus().equals(AdviceStatusEnum.ERROR)) {
      responseEntity = new ResponseEntity<>(response, headers, HttpStatus.ACCEPTED);
} else {
      responseEntity = new ResponseEntity<>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
}
```

```java
if (isStatusError(response)) {
      responseEntity = new ResponseEntity<>(response, headers, HttpStatus.OK);
} else {
      responseEntity = new ResponseEntity<>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
}

...

boolean isStatusError(Response response) {
   return null != response && response.getAdvice() != null && !response.getAdvice().getStatus().equals(AdviceStatusEnum.ERROR);
}
```

# Functions
## Use Descriptive Names
Fully describe what the function does in it's name. 


## Function Arguments

### Good
* No arguments - niladic
* Single argument - monadic
* Two arguments - dyadic
* Three arguments - triadic
### Bad


## Avoid output argumnets and in general avoid modifying arguments
Arguments -> for input 
Returning object -> for output

## Try to keep the arguments on the same level of abstraction as the function
```java
Overdraft calculateOverdraftLimitForClient(Client client, ByteArraysOutputStram transactionsFile); //not OK
```
## Avoid boolean parameters
* function does more than one thing and violates SPR 
* makes the call hard to read 
```java
activateUser(user, true); //what does true mean here??`
```
* often happens to big old functions that have to change 

## Consider wrapping some of the function arguments in a class when appropriate
```diff
-Circle makeCircle(double x, double y, double radius);
+Circle makeCircle(Point center, double radius);
```
Always search for the occurance of the same params that are extracted, to be able to replace them with the new class.
The logic related to this parameters will move to the new class.

## Check the input parameters - fail as fast as possible
```java
BigDecimal devide(Integer divident, Integer divisor) {
   Objects.requireNonNull(divident);
   Objects.requireNonNull(divisor);
   ...
}
```

## Return types

```void``` is suspicious.

Any method returning void is either meaningless or operates through side-effects, such as writing to display, network, file or database

## Don't return null
Use Optional to express that the function can return null value

```java
Optional<Person> findByName(String name) {
   ...
}
```


## Functions either return the value that they are mean to produce or throw an error (or return Try monad)
## void functions either complete successfully or throw an error (or return Try monad)

## Don't return NULL to "express" that something went wrong.
## Don't return a "message" to tell if the function has complete successfully or something went wrong.
## Minimize the number of `return`s in a function.

# Side Effects

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

## Caching should be easily turned off

## Pure functions

1. The function does not chnage anything
2. The function does not depend on anything that can change

```java
int doubleIt(int number) {
   return number * 2;
}
```

```java
int multiplyIt(int number, int factor) {
   return number * factor;
}
```

```java
int factor = 3;
int multiplyIt(int number) { //TODO add comments
   return number * factor;
}
```

## Separate side effects from business logic

>You get on the train
>
>&darr;
>   
>The train goes from A to B
>
>&darr;
>
>You get off the train

> [side effect] Get all of the input from database, rest service, etc.
>
>&darr;
>   
> [pure functions] Do all of the business logic, calculations, etc.
>
>&darr;
>
> [side effect] Persists in the database, file, etc.

Try to split functions to one pure function and up to two(input and output) impure functions. 

```java
class OrdersService {
   private final OrdersRepository ordersRepository;
   private final AuditLogRepository auditLogRepository;
   private final InventoryGateway inventoryGateway;
   OrdersService(OrdersRepository ordersRepository, InventoryGateway inventoryGateway, AuditLogRepository auditLogRepository) {
      this.ordersRepository = ordersRepository;
      this.inventoryGateway = inventoryGateway;
      this.auditLogRepository = auditLogRepository;
   }
   
   void processOrder(Long orderId) {
      Order order = this.ordersRepository.getById(orderId);                                                 //side effect (input) no business logic allowed
      ItemsAvailablility itemsAvailablility = this.inventoryGateway.checkAvailability(order.getItems());    //side effect (input) no business logic allowed

      ProcessedOrder processedOrder = order.process(itemsAvailablility);                                    // pure function - business logic

      this.ordersRepository.save(processedOrder);                                                           //side effect (output) no business logic allowed
      this.auditLogRepository.log(processedOrder);                                                          //side effect (output) no business logic allowed
   }
}
```

## Jva Streams best practices
* Avoid passing streams around, pass collections
* Don't mutate data in a stream (in peek and forEach)
* Don't throwing exceptions in streams - use either or Try monad
* One stream method call per line
```java
// BAD CODE:
strings.stream().filter(s -> s.length() > 2).sorted()
	.map(s -> s.substring(0, 2)).collect(Collectors.toList());
```
```java
// GOOD CODE:
strings.stream()
	.filter(s -> s.length() > 2)
	.sorted()
	.map(s -> s.substring(0, 2))
	.collect(Collectors.toList());
```
* Use `IntStream`, `LongStream` and `DoubleStream` when working with primitive types. They are faster (they avoid boxing) and easier to use (they add useful methods like sum)

# Error Handling

## Don't ignore exceptions! In the rear cases when this is needed - add a comment.
## At least log them. 

## Use Exceptions Rather Than Return Codes

## Catch or Pass 
### Catch exceptions only if you know what to do with them. Else let them "buble up" and show them to the caller (or not).

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

### Don't base `return` logic on try-catch
```diff
try {

} catch(SomeException e) {
   logger.error(...);
-   return ...
+   throw e
}
``` 

## Use Unchecked Exceptions
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
## Vavr Try and Either
https://www.vavr.io/vavr-docs/

### Either

Either represents a value of two possible types. An Either is either a Left or a Right.
```java
private static Either<ArithmeticException, BigDecimal> devide(int divident, int divisor) {
   BigDecimal dividentDecimal = BigDecimal.valueOf(divident);
   BigDecimal divisorDecimal = BigDecimal.valueOf(divisor);
   return divisor != 0 ?
            Either.right(dividentDecimal.divide(divisorDecimal)) :
            Either.left(new ArithmeticException("Devision by zero"));
}

public static void main(String[] args) {
   Either<ArithmeticException, BigDecimal> ok = devide(1, 5);
   Either<ArithmeticException, BigDecimal> error = devide(1, 0);

   Either<ArithmeticException, BigDecimal> mappedOk = ok.map(result -> result.multiply(BigDecimal.TEN));
   System.out.println(mappedOk); // Right(2)

   Either<ArithmeticException, BigDecimal> mappedError = error.map(result -> result.multiply(BigDecimal.TEN)); //multiply will not be applied
   System.out.println(mappedError); // Left(ArithmeticException)
}
```
### Try
```java
// = Success(result) or Failure(exception)
Try<Integer> divide(Integer dividend, Integer divisor) {
    return Try.of(() -> dividend / divisor);
}
```

```java
Try<Response> response = Try.of(() -> ...);

Integer chainedResult = response
      .map(this::actionThatTakesResponseAndReturnsInt)
      .getOrElse(defaultChainedResult);
```

```java
Try<String> tryDivide = devide(1, 5)
                .onSuccess(TrySample::log)         //separation of concerns
                .onFailure(TrySample::sendMail)
                .map(String::valueOf)               //pure functions
                .map(String::toUpperCase);         // this will be executed only when the Try is a Success else the error is just propagated
        System.out.println(tryDivide); //Success(0.2)

//        tryDivide.get();        // don't do that
//        tryDivide.getCause();   // don't do that
//
//        tryDivide.toEither();
        
        Try<Integer> tryOfInteger = tryDivide.flatMap(TrySample::someFunnctionThatReturnTry);
```

`Try` will be supported by Spring declarative transactions since version 5.2 - https://github.com/spring-projects/spring-framework/issues/20361


# Objects

## Keep logic in it's domain

TODO https://www.youtube.com/watch?v=MEySjYD86PQ  - addToOrder code from 23:00 - procedural vs OOP - move to a different section

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

## Use dependency injection
* Testing can be performed using mock objects.
* Loosely couple architecture.
* Makes it easier to remove all knowledge of a concrete implementation that a class needs to use.

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
/removing a lock
account.getLocks().remove(lockToRemove);
if(accounts.getLocks().isEmpty()) {
   account.setStatus(Active);
}
```

Instead

```java
class Account {
   Status status; // [Active, Inactive, Locked]
   List<Lock> locks; 
   // No getter setter, addLock(Lock), removeLock(Lock)

   Status getStatus() {
      if(!this.locks.isEmpty()) return Status.Locked;
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
Immutable objects are inherently thread-safe; they require no synchronization.

## Reuse common immutable objects (use static factories to cache)

```java
public static final Complex ZERO = new Complex(0, 0);
public static final Complex ONE = new Complex(1, 0);
public static final Complex I = new Complex(0, 1);
```

## Avoid generating getters and setters right away
If you have to do something with the memebers of a class - implement this logic into the class itself, don't expose the member.

```java
class Order {
   private List<OrderItem> items;

   boolean doesContainExpensiveItem(BigDecimal limit) {
      return this.items.stream().anyMatch(item -> item.getPrice().isMoreThan(limit));
   }

   boolean areAllItemsAvailable(Predicate<OrderItem> isAvailable) { // checking availability is a side effect(e.g. checking in a database) that's why we use higher order function
      return this.items.allMatch(isAvailable)
   }
}
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

   public Optional<String> getAddress() { // can be null - forse the users of the class to check it
      return Optional.ofNullable(this.address)
   }
}
```

## How to make an immutable class
```java
public final class ImmutableStudent { // prevent inheritance
   private final int id;             // fields are final
   private final String name;
   private final Dissertation dissertation;
   private final List<UniversityClass> classes;        
      
   //fileds are set only in constructor or a factory menthod
   public ImmutableStudent(int id, String name, Dissertation dissertation, List<UniversityClass> classes) { 
      this.name = name;
      this.id = id;
      this.dissertation = dissertation;
      this.classes = classes;
   }
   public int getId() { // there are getters but no setters
      return id;
   }
   public String getName() {
      return name;
   } 
   // a copy of referenced objects are created to prevet someone who owns a reference to the object to chnage it
   public Dissertation getDissertation() { 
      return dissertation.clone();
   } 

   // prevent someone adding or deeting from the list
   public List<UniversityClass> getClasses() {
      return Collections.unmodifiableList(this.classes);
   }

   // to mutate an immutable object - copy it
   public ImmutableStudent withName(String name) { 
      return new ImmutableStudent(this.id, name, this.dissertation);
   }
}
```

## Immutable objects are:
* Easier to reason about - NO OBJECTS IN IVALID STATE
* Easier to share/cache
* Thread-safe - no synchronisation needed
* Good Map keys and Set elements, since these typically do not change once created (also the hash method can be cached or precomputed for better performance)

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
            throw new NoOrderItemsException("You have to add items, Bro!");
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
      cheeseTopping = new CheeseTopping();
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



https://www.youtube.com/watch?v=-lVVfxsRjcY (30:00)
# API/Module design

## A module, class or function should work correctly by itself and should not depend on the result some other module, class, function or variable

This will make `someFunction` unusable in other parts of the code. `someFunction` is tightly couple to the piece of code that provides `order`
```java
void someFunction(Order order) { //This is a bad code!!! 
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

## Wrap 3th party librabries
* Minimize your dependencies upon it: You can choose to move to a different library in the future. 
* Makes it easier to mock out third-party calls when you are testing your own code.

### make each class or member as inaccessible as possible
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

If a class has a dependency that has a couple of methods but only 1-2 are used - conside defineing an interface that has only this 1-2 methods.

```java
class SomeService {
   private PersonService personService; //the interface PersonService should not contain methods that are not used by SomeService

   void someMethod() {
      this.personService.findById(id); //nothig else from personService is used in this class
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

Function Arguments
```diff
-void checkPersonIdentificationNumber(Person person) { //to test the function a whole person is needed
-   String identNumber = person.getIdentNumber(); //person has a lot more fields
-   ...
-}
+void checkPersonIdentificationNumber(String identNumber) {
+   ...
+}
```


# SOLID

## S — Single responsibility principle

A module or class should have responsibility over a single part of the functionality provided by the software.
A class or module should have one, and only one, reason to be changed.

https://itnext.io/solid-principles-explanation-and-examples-715b975dcad4

```C#
class User 
{
    void CreatePost(Database db, string postMessage)
    {
        try
        {
            db.Add(postMessage);
        }
        catch (Exception ex)
        {
            db.LogError("An error occured: ", ex.ToString());
            File.WriteAllText("\LocalErrors.txt", ex.ToString());
        }
    }
}
```
* create a new post
* log an error in the database
* and log an error in a local file

## Solution
```C#
class Post {
    private ErrorLogger errorLogger; // injected
    void CreatePost(Database db, string postMessage) {
        try {
            db.Add(postMessage);
        }
        catch (Exception ex) {
            errorLogger.log(ex.ToString())
        }
    }
}

class ErrorLogger {
    void log(string error) {
      db.LogError("An error occured: ", error);
      File.WriteAllText("\LocalErrors.txt", error);
    }
}
```
two classes that each has one responsibility:
* to create a post 
* to log an error

Another example is class from the web layer (e.g. Spring @Controller) to have business logic in it, or a class from the service layer to have something different than business logic in it(e.g. data access)

## O — Open/closed principle

software entities (classes, modules, functions, etc.) should be open for extensions, but closed for modification

```C#
class Post {
    void CreatePost(Database db, string postMessage) {
        if (postMessage.StartsWith("#")) {
            db.AddAsTag(postMessage);
        } else {
            db.Add(postMessage);
        }
        // what if post starts with "@" or "$"
    }
}
```

```C#
class Post {
    void CreatePost(Database db, string postMessage) {
        db.Add(postMessage);
    }
}

class TagPost : Post {
    override void CreatePost(Database db, string postMessage) {
        db.AddAsTag(postMessage);
    }
}

// The evaluation of the first character ‘#’ will now be handled elsewhere 
// If "@" posts have to be added - this will be done with another class without chnaging Post
```

## L — Liskov substitution principle

Objects in a program should be replaceable with instances of their subtypes without altering the correctness of that program

```C#
class Post{
    void CreatePost(Database db, string postMessage) {
        db.Add(postMessage);
    }
}

class TagPost : Post{
    override void CreatePost(Database db, string postMessage) {
        db.AddAsTag(postMessage);
    }
}

class MentionPost : Post {
    void CreateMentionPost(Database db, string postMessage) {
        string user = postMessage.parseUser();

        db.NotifyUser(user);
        db.OverrideExistingMention(user, postMessage);
        base.CreatePost(db, postMessage);
    }
}

class PostHandler {
    private database = new Database();

    void HandleNewPosts() {
        List<string> newPosts = database.getUnhandledPostsMessages();

        foreach (string postMessage in newPosts) {
            Post post;
            if (postMessage.StartsWith("#")) {
                post = new TagPost();
            }
            else if (postMessage.StartsWith("@")) {
                post = new MentionPost();
            }
            else {
                post = new Post();
            }
            post.CreatePost(database, postMessage);
        }
    }
}
```

```C#
class MentionPost : Post
{
    override void CreatePost(Database db, string postMessage) {
        string user = postMessage.parseUser();

        NotifyUser(user);
        OverrideExistingMention(user, postMessage)
        base.CreatePost(db, postMessage);
    }

    private void NotifyUser(string user) {
        db.NotifyUser(user);
    }

    private void OverrideExistingMention(string user, string postMessage) {
        db.OverrideExistingMention(_user, postMessage);
    }
}
```

## D - Dependency inversion principle
High-level modules should not depend on low-level modules. Both should depend on abstractions.

```C#
class Post
{
    private ErrorLogger errorLogger = new ErrorLogger(); //this violates the principle - If we wanted to use a different kind of logger, we would have to modify the class

    void CreatePost(Database db, string postMessage)
    {
        try
        {
            db.Add(postMessage);
        }
        catch (Exception ex)
        {
            errorLogger.log(ex.ToString())
        }
    }
}
```

## Solution

```C#
class Post
{
    private Logger logger;

    public Post(Logger injectedLogger) //injected
    {
        logger = injectedLogger;
    }

    void CreatePost(Database db, string postMessage)
    {
        try
        {
            db.Add(postMessage);
        }
        catch (Exception ex)
        {
            logger.log(ex.ToString())
        }
    }
}
```


# Code smells
https://www.youtube.com/watch?v=D4auWwMsEnY
https://www.industriallogic.com/wp-content/uploads/2005/09/smellstorefactorings.pdf
## Bloaters
Bloaters are code, methods and classes that have increased to such gargantuan proportions that they are hard to work with. Usually these smells do not crop up right away, rather they accumulate over time as the program evolves (and especially when nobody makes an effort to eradicate them).
1. Long Method
2. Large Class
3. **Primitive Obsession**
4. Long Parameter List
5. **Data Clumps**

### Primitive Obsession
Passing around objects that are too dumm (primitive) and peaces of code decide what to do with them based on some context that is not enforsed.  
Remember state representation.

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

## Object-Orientation Abusers
All these smells are incomplete or incorrect application of object-oriented programming principles.
1. **Switch Statements**
2. **Temporary Field**
3. Refused Bequest
4. Alternative Classes with Different Interfaces

## Change Preventers
These smells mean that if you need to change something in one place in your code, you have to make many changes in other places too. Program development becomes much more complicated and expensive as a result.
1. Divergent Change
2. **Shotgun Surgery**
3. Parallel Inheritance Hierarchies

## Dispensables
A dispensable is something pointless and unneeded whose absence would make the code cleaner, more efficient and easier to understand.
1. Comments
2. **Duplicate Code**
3. Lazy Class
4. **Data Class**
5. Dead Code
6. **Speculative Generality**

### Speculative Generality
A code that is written because of feature that might arrive in the future.
We're bad guessers!
It makes the code hard to reason about. Code is read more often than it's written.
Increases abstraction.

**Premature Optimization Is the Root of All Evil**
A code that is written in a certain way because it will be more performant. 

Source of bad performance:
* IO us slower than memory and CPU by a factor of millions
* Objects that are meant to be created once
   * Pools (Connection pools, Thread pools)
   * Factories 
   * Jackson ObjectMapper
* Regexes might be slow

Don't do something just because you think it's slow. The JVM optimizes a lot under the hood
Iterating a List of 10 elements is super fast, dont need to wori about it!

**Write code that works (and is readable, testable and maintainable) then use profilers and to find out where the problem is.**
* VisualVM
* FlightRecorder - [link](https://www.baeldung.com/java-flight-recorder-monitoring)
 

## Couplers
All the smells in this group contribute to excessive coupling between classes or show what happens if coupling is replaced by excessive delegation.
1. Feature Envy
2. Inappropriate Intimacy
3. **Message Chains**
4. Middle Man
5. Incomplete Library Class

## Other

### Noisy logging
* Log errors/exceptions  
* Log input, output
* Log Side effects
* Avoid logging computed values/results of pure functions - e.g. the "input is 1 and 2" "the sum = 3"

# Sources

1. Clean Code by Uncle Bob Martin
2. https://sourcemaking.com/refactoring/smells
3. https://itnext.io/solid-principles-explanation-and-examples-715b975dcad4