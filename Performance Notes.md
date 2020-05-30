# Performance Notes

## Reflection DogTags
I wrote three classes to measure the performance of DogTags, and compare their performance with Apache's `EqualsBulder.referenceEqual()`. This gave me an opportunity to try a few different approaches to get the best possible performance out of my code. Here's what I learned.

### 1 Wrapper fields outperform primitive fields.
When I used wrapper classes like Integer for all my fields, DogTags comparisons ran more than twice as fast as when I used primitives like int. While I didn't figure out the reason, I ruled out one candidate. My early code used `Field.get()` for primitives, so I though it might be spending too much time boxing and unboxing primitive values. But I rewrote it to use Fields primitive getters like `Field.getInt()`, which returns an int, not an Integer. This had no effect on performance. Wrapper classes still outperformed primitives. Of course, there are good reasons why returning wrapper classes are generally considered a poor practice over primitives.

### 2 For speed, DogTags whips EqualsBuilder.referenceEquals(), but hand-coding whips DogTags
When I compare the speed difference of DogTags and `EqualsBuilder.referenceEquals()`, DogTags are anywhere from 5 to 50 times as fast, depending on how many fields get tested before it finds a mismatch. If the mismatch gets found early, DogTags can be about 50 times faster. But a hand-coded equals method is generally in the range of 25 times faster than a DogTag. Also, using EqualsBuilder to build an equals method, while is less convenient, it's also nearly as fast as hand-coding. EqualsBuilder and hand-coded equals methods aren't guaranteed to be consistent with hashCode(), but DogTags are. Consequently, I need to be clear that DogTags does not replace EqualsBuilder entirely, but it's a good, much faster replacement of EqualsBuilder's `reflectionEquals()` method.

### 3 Functional Programming degrades performance.
This may come as a surprise, but the reasons have little to do with the Stream classes or their methods. In order to convert DogTags to use functional programming, I needed to get rid of the two "Throwing" functional interfaces, which declare a method to throw a checked exception. So I first had to rewrite the implementing code to wrap the thrown exception and rethrow it. This code was never executed, because the Exception can't be thrown under the circumstances. But it's necessary because, to use a Stream, I need a method that qualifies as a Predicate, which does not throw a checked exception.

Once I made this change, but before I switched to functional code, I repeated my performance test, and discovered the code ran twice as slowly. Converting to functional programming didn't offer any speed improvements, and turning on parallel processing actually slowed down the code by a factor of 10 or so.

Here's the final loop of the doTestForEqual(T, T) written a functional programming style:

    return fieldProcessors.stream()
        .allMatch(
            f -> f.testForEquals(thisOneNeverNull, thatOneNeverNull)
        ); 

### Results

Here are the result s of a performance test comparing DogTags with `EqualsBuilder.reflectionEquals()`:

![png](https://github.com/SwingGuy1024/DogTags/blob/master/Performance.png)



## Lambda Expressions

The `DogTag.createByLambda()` method lets me specify the fields to include using method references instead of reflection. While this performs noticeably faster than reflection, it's not a big improvement, and is still much slower than Apache's EqualBuilder class. This surprised me, since the DogTags can bail out and return false as soon as a mismatching field is detected, while the EqualsBuilder needs to cycle through all the remaining fields (skipping the actaul testing) before it can return.

Furthermore, when written using a Stream, the performance gets even worse. Written as a stream, the final loop looks like this:

          return fieldHandlers.stream()
              .allMatch(tFieldHandler -> tFieldHandler.doEqual(thisOne, thatOneNotNull));
The imperatave implementation looks like this:

      for (FieldHandler<T> handler : fieldHandlers) {
        if (!handler.doEqual(thisOne, thatOneNotNull)) {
          return false;
        }
      }
      return true;

Here are the stats for the stream function:

                         DgTg	 R.Eq	   HC	 Eq.B	R.Eq/DgTg
      All are Equal:	  144	  639	   13	   20	   4.438
    Fields Tried 11:	  142	  640	   13	   21	   4.507
    Fields Tried 10:	  130	  613	   11	   18	   4.715
    Fields Tried  9:	  120	  597	   10	   16	   4.975
    Fields Tried  8:	  112	  577	   10	   16	   5.152
    Fields Tried  7:	  103	  558	   10	   15	   5.417
    Fields Tried  6:	   96	  534	   10	   16	   5.563
    Fields Tried  5:	   88	  516	   10	   16	   5.864
    Fields Tried  4:	   74	  491	    9	   14	   6.635
    Fields Tried  3:	   64	  469	    6	    9	   7.328
    Fields Tried  2:	   57	  448	    5	    9	   7.860
    Fields Tried  1:	   43	  430	    5	    9	  10.000
           Identity:	    7	    5	    5	    5	   0.714
    
    Key: DgTg: DogTags
         R.Eq: EqualsBuilder.referenceEqual()
           HC: Hand Coded
         Eq.B: new EqualsBuilder()

The imperative implementation gives these results:

                         DgTg	 R.Eq	   HC	 Eq.B	R.Eq/DgTg
      All are Equal:	   98	  652	   16	   21	   6.653
    Fields Tried 11:	   98	  651	   16	   22	   6.643
    Fields Tried 10:	   88	  622	   15	   20	   7.068
    Fields Tried  9:	   83	  600	   13	   18	   7.229
    Fields Tried  8:	   75	  581	   12	   18	   7.747
    Fields Tried  7:	   69	  564	   13	   18	   8.174
    Fields Tried  6:	   62	  544	   11	   18	   8.774
    Fields Tried  5:	   56	  523	   11	   18	   9.339
    Fields Tried  4:	   44	  500	   10	   16	  11.364
    Fields Tried  3:	   35	  477	    7	   11	  13.629
    Fields Tried  2:	   30	  454	    7	   11	  15.133
    Fields Tried  1:	   17	  431	    6	   11	  25.353
           Identity:	    3	    4	    6	    5	   1.333
    
    Key: DgTg: DogTags
         R.Eq: EqualsBuilder.referenceEqual()
           HC: Hand Coded
         Eq.B: new EqualsBuilder()

For comparison, using reflection gives these results:

                         DgTg	 R.Eq	   HC	 Eq.B	R.Eq/DgTg
      All are Equal:	  133	  651	   15	   21	   4.895
    Fields Tried 11:	  133	  651	   15	   21	   4.895
    Fields Tried 10:	  121	  626	   14	   19	   5.174
    Fields Tried  9:	  110	  601	   13	   17	   5.464
    Fields Tried  8:	   98	  582	   12	   17	   5.939
    Fields Tried  7:	   87	  562	   12	   17	   6.460
    Fields Tried  6:	   76	  543	   11	   17	   7.145
    Fields Tried  5:	   65	  522	   10	   17	   8.031
    Fields Tried  4:	   54	  497	   11	   15	   9.204
    Fields Tried  3:	   40	  475	    7	   11	  11.875
    Fields Tried  2:	   27	  452	    7	   10	  16.741
    Fields Tried  1:	   16	  432	    6	   10	  27.000
           Identity:	    1	    4	    6	    5	   4.000
    
    Key: DgTg: DogTags
         R.Eq: EqualsBuilder.referenceEqual()
           HC: Hand Coded
         Eq.B: new EqualsBuilder()

![png](https://github.com/SwingGuy1024/DogTags/blob/master/LambdaPerformance.png)
