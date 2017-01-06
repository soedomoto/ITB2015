import statistics as statistics
import unicodecsv as csv
from sklearn.neighbors import KNeighborsClassifier

def column(matrix, i):
    return [row[i] for row in matrix]


table = csv.reader(open('../Data/coord_nagari_2014.csv'))
table = list(table)
table = table[1:]

locations = {}
for id, lat, lon in table:
    locations[id] = (lat.strip(), lon.strip())


table = csv.reader(open('../Data/cost_matrix_nagari_2014.csv'))
table = list(table)
table = table[1:]

cost_matrix = {}
for id_a, id_b, distance, duration, status in table:
    cost_matrix.setdefault(id_a, {})
    cost_matrix[id_a][id_b] = (float(distance), float(duration))


table = csv.reader(open('../Data/enumerators.csv'))
table = list(table)
table = table[1:]

enumerators = {}
for id, lat, lon in table:
    enumerators[id] = (lat.strip(), lon.strip())


neigh = KNeighborsClassifier(n_neighbors=3)
neigh.fit(X, y)