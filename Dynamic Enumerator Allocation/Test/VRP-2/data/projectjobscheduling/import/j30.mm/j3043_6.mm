************************************************************************
file with basedata            : mf43_.bas
initial value random generator: 551462510
************************************************************************
projects                      :  1
jobs (incl. supersource/sink ):  32
horizon                       :  225
RESOURCES
  - renewable                 :  2   R
  - nonrenewable              :  2   N
  - doubly constrained        :  0   D
************************************************************************
PROJECT INFORMATION:
pronr.  #jobs rel.date duedate tardcost  MPM-Time
    1     30      0       25       16       25
************************************************************************
PRECEDENCE RELATIONS:
jobnr.    #modes  #successors   successors
   1        1          3           2   3   4
   2        3          3           7   8  10
   3        3          1          23
   4        3          2           5   6
   5        3          3           7  12  13
   6        3          1          21
   7        3          3           9  11  24
   8        3          2          13  15
   9        3          3          14  15  22
  10        3          2          17  19
  11        3          3          18  20  27
  12        3          2          16  25
  13        3          1          22
  14        3          2          18  27
  15        3          1          20
  16        3          1          24
  17        3          2          21  29
  18        3          3          19  25  26
  19        3          1          30
  20        3          2          21  23
  21        3          1          28
  22        3          2          23  26
  23        3          2          25  31
  24        3          3          27  28  29
  25        3          1          29
  26        3          2          28  31
  27        3          2          30  31
  28        3          1          30
  29        3          1          32
  30        3          1          32
  31        3          1          32
  32        1          0        
************************************************************************
REQUESTS/DURATIONS:
jobnr. mode duration  R 1  R 2  N 1  N 2
------------------------------------------------------------------------
  1      1     0       0    0    0    0
  2      1     5       0    5    6    5
         2     8       7    0    6    5
         3    10       0    5    4    4
  3      1     3       9    0    9    9
         2     5       4    0    8    7
         3     9       0    5    7    3
  4      1     1      10    0    5    9
         2     2       0    5    5    8
         3    10       0    5    3    8
  5      1     1       0    7    3    8
         2     4       0    6    2    5
         3     8       3    0    1    4
  6      1     3       0    9    8    7
         2     4       6    0    5    6
         3     8       0    4    4    6
  7      1     3       3    0    8    9
         2     3       3    0    7   10
         3     5       0    6    5    5
  8      1     1       3    0    8    1
         2     7       2    0    8    1
         3     9       2    0    3    1
  9      1     2       0    7    9    9
         2     3       6    0    9    8
         3     6       6    0    8    7
 10      1     1       7    0   10    8
         2     4       0    9    8    6
         3    10       0    8    6    1
 11      1     5       6    0    8    7
         2     6       0    2    5    4
         3     7       6    0    3    2
 12      1     2       9    0    5    9
         2     8       0    7    4    8
         3     8       0    8    1    8
 13      1     1       7    0    6    6
         2     2       0    7    5    5
         3     3       6    0    4    2
 14      1     2       7    0    8    7
         2     7       0    8    7    7
         3     9       4    0    3    3
 15      1     1       0    4    8    8
         2     9       2    0    7    8
         3    10       0    3    4    7
 16      1     6       0    3    6    6
         2     6       9    0    6    4
         3     6       8    0    7    4
 17      1     2      10    0    1    7
         2     2       0    6    1    8
         3    10       7    0    1    6
 18      1     1       0    9    5    4
         2     2       0    8    4    3
         3     3       5    0    1    3
 19      1     2       3    0    5    9
         2     4       0    4    5    9
         3     4       3    0    5    7
 20      1     3       8    0    5    3
         2     6       0    4    5    2
         3     7       0    3    2    1
 21      1     4       5    0    3    7
         2     5       4    0    2    6
         3     6       3    0    2    5
 22      1     3       8    0    9    5
         2     5       8    0    8    5
         3     6       7    0    7    5
 23      1     2       6    0    4    8
         2     8       0    7    4    7
         3     9       0    2    4    5
 24      1     1       8    0    7    7
         2     5       0    7    7    7
         3     6       0    5    7    5
 25      1     4       8    0    7    8
         2     6       5    0    6    5
         3    10       1    0    5    4
 26      1     3       7    0    9    9
         2     4       7    0    7    9
         3     9       7    0    6    8
 27      1     4       0   10    8    6
         2     7       0    6    5    5
         3     9      10    0    3    3
 28      1     3       0    6    9    9
         2     5       7    0    8    9
         3    10       7    0    8    8
 29      1     1       0    4    9   10
         2     3       0    4    6    5
         3     4       0    3    5    5
 30      1     2       8    0    3    9
         2     3       0    6    2    7
         3     5       0    3    2    5
 31      1     4       7    0   10    9
         2     8       0    1   10    6
         3     9       6    0   10    4
 32      1     0       0    0    0    0
************************************************************************
RESOURCEAVAILABILITIES:
  R 1  R 2  N 1  N 2
   26   27  166  180
************************************************************************
