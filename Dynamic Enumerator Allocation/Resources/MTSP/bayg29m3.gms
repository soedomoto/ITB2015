$ontext

   bayg29 29 city problem  with five salesman

   Erwin Kalvelagen, Amsterdam Optimization

   References:
       Bektas, T. (2006). The multiple traveling salesman problem: an overview of formulations and
        solution procedures. OMEGA: The International Journal of Management Science, 34(3), 209-219.

$offtext

$eolcom //


$set startcity i13

scalar
   m 'number of salesman' /5/
   p 'number of cities to visit' /6/
;

sets
   i /i1*i29/
;
alias (i,j);

table c(i,j) 'distance matrix (KM)'


      i1  i2  i3  i4  i5  i6  i7  i8  i9 i10 i11 i12 i13 i14 i15 i16 i17 i18 i19 i20 i21 i22 i23 i24 i25 i26 i27 i28 i29
 i1       97 205 139  86  60 220  65 111 115 227  95  82 225 168 103 266 205 149 120  58 257 152  52 180 136  82  34 145
 i2          129 103  71 105 258 154 112  65 204 150  87 176 137 142 204 148 148  49  41 211 226 116 197  89 153 124  74
 i3              219 125 175 386 269 134 184 313 201 215 267 248 271 274 236 272 160 151 300 350 239 322  78 276 220  60
 i4                  167 182 180 162 208  39 102 227  60  86  34  96 129  69  58  60 120 119 192 114 110 192 136 173 173
 i5                       51 296 150  42 131 268  88 131 245 201 175 275 218 202 119  50 281 238 131 244  51 166  95  69
 i6                          279 114  56 150 278  46 133 266 214 162 302 242 203 146  67 300 205 111 238  98 139  52 120
 i7                              178 328 206 147 308 172 203 165 121 251 216 122 231 249 209 111 169  72 338 144 237 331
 i8                                  169 151 227 133 104 242 182  84 290 230 146 165 121 270  91  48 158 200  39  64 210
 i9                                      172 309  68 169 286 242 208 315 259 240 160  90 322 260 160 281  57 192 107  90
 i10                                         140 195  51 117  72 104 153  93  88  25  85 152 200 104 139 154 134 149 135
 i11                                             320 146  64  68 143 106  88  81 159 219  63 216 187  88 293 191 258 272
 i12                                                 174 311 258 196 347 288 243 192 113 345 222 144 274 124 165  71 153
 i13                                                     144  86  57 189 128  71  71  82 176 150  56 114 168  83 115 160
 i14                                                          61 165  51  32 105 127 201  36 254 196 136 260 212 258 234
 i15                                                             106 110  56  49  91 153  91 197 136  94 225 151 201 205
 i16                                                                 215 159  64 126 128 190  98  53  78 218  48 127 214
 i17                                                                      61 155 157 235  47 305 243 186 282 261 300 252
 i18                                                                         105 100 176  66 253 183 146 231 203 239 204
 i19                                                                             113 152 127 150 106  52 235 112 179 221
 i20                                                                                  79 163 220 119 164 135 152 153 114
 i21                                                                                     236 201  90 195  90 127  84  91
 i22                                                                                         273 226 148 296 238 291 269
 i23                                                                                             112 130 286  74 155 291
 i24                                                                                                 130 178  38  75 180
 i25                                                                                                     281 120 205 270
 i26                                                                                                         213 145  36
 i27                                                                                                              94 217
 i28                                                                                                                 162
 i29
;



c(i,j) = max(c(i,j),c(j,i));

set arcs(i,j);
arcs(i,j)$c(i,j) = yes;

set i0(i) /%startcity%/;

set i2(i);
i2(i)$(not i0(i)) = yes;

scalar n 'number of nodes';
n = card(i);

binary variables x(i,j);
positive variables u(i);
variable z;

equations
   start
   end
   assign1(i)
   assign2(j)
   sec(i,j)    'subtour elimination'
   cost
;



start.. sum(i2(j), x('%startcity%',j)) =e= m;

end.. sum(i2(i), x(i,'%startcity%')) =e= m;

assign1(i2(i)).. sum(arcs(i,j), x(i,j)) =e= 1;

assign2(i2(j)).. sum(arcs(i,j), x(i,j)) =e= 1;

sec(arcs(i,j))$(i2(i) and i2(j)).. u(i) - u(j) + p*x(i,j) =L= p-1;

cost.. z =e= sum(arcs, c(arcs)*x(arcs));

option optcr=0;
option iterlim=10000000;

model mtsp/all/;
solve mtsp minimizing z using mip;

display x.l;

table xy(i,*)
              x       y
   i1    1150.0  1760.0
   i2     630.0  1660.0
   i3      40.0  2090.0
   i4     750.0  1100.0
   i5     750.0  2030.0
   i6    1030.0  2070.0
   i7    1650.0   650.0
   i8    1490.0  1630.0
   i9     790.0  2260.0
  i10     710.0  1310.0
  i11     840.0   550.0
  i12    1170.0  2300.0
  i13     970.0  1340.0
  i14     510.0   700.0
  i15     750.0   900.0
  i16    1280.0  1200.0
  i17     230.0   590.0
  i18     460.0   860.0
  i19    1040.0   950.0
  i20     590.0  1390.0
  i21     830.0  1770.0
  i22     490.0   500.0
  i23    1840.0  1240.0
  i24    1260.0  1500.0
  i25    1280.0   790.0
  i26     490.0  2130.0
  i27    1460.0  1420.0
  i28    1260.0  1910.0
  i29     360.0  1980.0
;



parameter tour(*,*);
set kall/k1*k10000/;
set kk(kall) /k1/;

loop((i,j)$(x.l(i,j)>0.5),
   tour(kk,'x0') = xy(i,'x');
   tour(kk,'y0') = xy(i,'y');
   tour(kk,'x1') = xy(j,'x');
   tour(kk,'y1') = xy(j,'y');
   kk(kall)=kk(kall-1);
);

display tour;



