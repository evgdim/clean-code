# <center>The only way to go fast is to go well</center>
<center>Evgeni Dimitrov</center>

- [Overview](#overview)
   - [Questions to answer](#questions-to-answer)
   - [When to reafactor](#when-to-reafactor)
- [Names](#names)
   - [Scope](#Scope)
      - [Variable names should be proportional to their scope](#Variable-names-should-be-proportional-to-their-scope)

# Overview

## Questions to answer
* Is my code easy to read?
* Is my code easy to unit test?
* How easy is to find bugs and chnage functionality?

## When to reafactor
1. Right after the code is working and the unit tests are done
2. In the scope of a rlatevly big chnage
3. Before the testing has started

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

# Functions

## Small

### ~~Function should fit on the screen.~~
<img src="retro_computer.png" alt="drawing" width="200"/>

### ***Function has the right size when you cannot extract anything more from it as a function.***


### You should be able to explain what a function does in no more than 20 words without using words like “and” and “or”. 


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
## Reading Code from Top to Bottom: The Stepdown Rule
TODO

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

# API/Module desfing

TODO: Desing a class or module like a library.

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
