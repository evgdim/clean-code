# <center>The only way to go fast is to go well</center>
<center>Evgeni Dimitrov</center>

# Overview

## Questions to answer
* Is my code easy to read?
* Is my code easy to unit test?
* Is my code easy to reason?
* Is my application easy to deploy?
* How easy is to find bugs and chnage functionality?

## When to reafactor
1. Right after the code is working and the unit tests are done
2. In the scope of a rlatevly big chnage
3. Before the testing has started

# Docs

`README.md` or even a folder full of readmes

* Branching strategy (Git Flow / Trunk based development)
* How to setup a development environmnt
   * Databse instalation (scripts should be available in the source control)
   * Mocks of 3th party systems
* How to deploy to the different environments
   * Urls, accounts used, etc
* Tricky parts of the application
* Conventions used in the project
   * `Databse table names should be in plural`
   * `Rest endpoints should be in plural and represent a resource`
   * `The database scripts should be placed in folder <project root>/databse`
   

[Names >](#names)
<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>

# Names

## Scope
### Variable names should be proportional to their scope 
```java
for(int i = 0; i < 10; i++) {
   ...
}
```

### Avoid single letter variables 
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

## Avoid Mental Mapping

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
Avoid prefixing member variables with “m_” . Your classes and functions should be small enough that you don’t need them.

## Class names

## Branch names, commit mesages, PR descriptions, etc

Try to prefix names, mesages and descriptions with an issue identifier if the code change is related.

<pre>
git commit -m"<b>DATAJPA-245</b> Support upsert operations in CRUD repository"
</pre>

[Functions >](#functions)
<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>

# Functions

## Small

### ~~Function should fit on the screen.~~
<img src="retro_computer.png" alt="drawing" width="200"/>

### ***Function has the right size when you cannot extract anything more from it as a function.***


### You should be able to explain what a function does in no more than 20 words without using words like “and” and “or”. 

### Don't use a `{}` for lambdas!

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

TODO: Add example with car direction from https://tedvinke.wordpress.com/2017/11/24/functional-java-by-example-part-2-tell-a-story/

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

## Use Descriptive Names
Fully describe what the function does in it's name. 

## Function Arguments

### Good
* No arguments - niladic
* Single argument - monadic
* Two arguments - dyadic
* Three arguments - triadic
### Bad

### Avoid output argumnets
### Try to keep the arguments on the same level of abstraction as the function
### Avoid boolean parameters - this implies that the function does more than one thing
### Consider wrapping some of the function arguments in a class when appropriate
```diff
-Circle makeCircle(double x, double y, double radius);
+Circle makeCircle(Point center, double radius);
```

## Return types

```void``` is a code smell.

Any method returning void is either meaningless or operates through side-effects, such as writing to display, network, file or database

[Side Effects >](#Side-Effects)
<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
## Side Effects
### Extract environment (Date-time, properties, current OS)

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
   //Good luck
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
### Caching should be easily turned off

### Pure functions

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
int multiplyIt(int number) {
   return number * factor;
}
```

```java
final int factor = 3;
int multiplyIt(int number) {
   return number * factor;
}
```

### Separate side effects from business logic

>You get on the train
>
>&darr;
>   
>The train goes from A to B
>
>&darr;
>
>You get of the train

> [side effect] Get all of the input from database, rest service, etc.
>
>&darr;
>   
> [pure functions] Do all of the business logic, calculations, etc.
>
>&darr;
>
> [side effect] Persists in the database, file, etc.

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

## Use Exceptions Rather Than Return Codes

## Use Unchecked Exceptions
### Checked Exception violate the Open/Closed Principle
If you throw a checked exception from a method in your code and the catch is three levels
above, you must declare that exception in the signature of each method between you and
the catch. This means that a change at a low level of the software can force signature
changes on many higher levels.

### Checked exceptions breaks encapsulation
All functions in the path of a throw must know about details of that low-level exception.

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
TODO vavr Try


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


# Objects
## Prefer factory methods when multiple constructors are needed
TODO
## Use dependency injection
* Testing can be performed using mock objects.
* Loosely couple architecture.
* Makes it easier to remove all knowledge of a concrete implementation that a class needs to use.

# API/Module desing

TODO: Desing a class or module like a library.
System of systems
visibility default is package
https://www.youtube.com/watch?v=MEySjYD86PQ  - addToOrder code from 23:00

TODO: statuses to inheritance example: e.g. NewOrder -> ApprovedOrder -> DeliveredOrder, etc.

## Wrap 3th party librabries
* Minimize your dependencies upon it: You can choose to move to a different library in the future. 
* Makes it easier to mock out third-party calls when you are testing your own code.

# make each class or member as inaccessible as possible
Create an interface that returns private classes that are not visible
# Ensure clear interfaces between components - Interfaces and function arguments should be cohesive enough
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
-void checkPersonIdentificationNumber(Person person) {
-   String identNumber = person.getIdentNumber(); //person has a lot more fields
-   ...
-}
+void checkPersonIdentificationNumber(String identNumber) {
+   ...
+}
```




# Mutability is the new GOTO
Mutability should be avoided or "pushed" to lower level.

```java
List<Integer> numbers = Arrays.asList(1,2,3,4,5,6,7,8,9);
```

```java
int total = 0;
for(int i = 0; i < number.size(); i++) {
   if(numbers.get(i) % 2 == 0) {
      total += numbers.get(i) * 2;
   }
}

logger.info(total);
```

```java
int total = numbers.stream()
                   .filter(n -> n % 2 == 0)
                   .mapToInt(n -> n * 2)
                   .sum();
```


## Avoid generating getters and setters right away

TODO
example about rowmapper and toParameters

TODO Law of Demeter

# Premature Optimization Is the Root of All Evil

## Source of bad performance:
* IO us slower than memory and CPU by a factor of millions
* Objects that are meant to be created once
   * Pools (Connection pools, Thread pools)
   * Factories 
   * Jackson ObjectMapper
* Regexes might be slow


## Write code that works (and is readable, testable and maintainable) then use profilers and to find out where the problem is. 
* VisualVM
* FlightRecorder - [link](https://www.baeldung.com/java-flight-recorder-monitoring)

## Don't do something just because you think it's slow. The JVM optimizes a lot under the hood 

# Code smells

## Bloaters
Bloaters are code, methods and classes that have increased to such gargantuan proportions that they are hard to work with. Usually these smells do not crop up right away, rather they accumulate over time as the program evolves (and especially when nobody makes an effort to eradicate them).
1. Long Method
2. Large Class
3. **Primitive Obsession**
4. Long Parameter List
5. **Data Clumps**

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
4. Data Class
5. Dead Code
6. **Speculative Generality**

## Couplers
All the smells in this group contribute to excessive coupling between classes or show what happens if coupling is replaced by excessive delegation.
1. Feature Envy
2. Inappropriate Intimacy
3. Message Chains
4. Middle Man
5. Incomplete Library Class



# Sources

1. Clean Code by Uncle Bob Martin
2. https://sourcemaking.com/refactoring/smells
