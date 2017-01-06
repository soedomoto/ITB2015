import statistics as statistics
import unicodecsv as csv

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


nodes = []
idx = 0
for id, (lat, lon) in enumerators.items():
    nodes.append((idx, lon, lat, id))
    idx += 1

for id, (lat, lon) in locations.items():
    nodes.append((idx, lon, lat, id))
    idx += 1


with open('../Data/enumeration_problem_octaplanner-n182-k15.vrp', 'wb') as f:
    lines = [
        'NAME: {}'.format('pessel-road-time-n182-k15'),
        'COMMENT: {}'.format('Generated for OptaPlanner VRP Test with Python.'),
        'TYPE: {}'.format('CVRP'),
        'DIMENSION: {}'.format(len(nodes)),
        'EDGE_WEIGHT_TYPE: {}'.format('EXPLICIT'),
        'EDGE_WEIGHT_FORMAT: {}'.format('FULL_MATRIX'),
        'EDGE_WEIGHT_UNIT_OF_MEASUREMENT: {}'.format('sec'),
        'CAPACITY: {}'.format(int(len(locations) / len(enumerators)) + 1),
    ]

    lines.append('NODE_COORD_SECTION')
    for idx, lon, lat, id in nodes:
        lines.append('{} {} {} {}'.format(idx, lon, lat, id))

    lines.append('EDGE_WEIGHT_SECTION')
    for idx_a, lon_a, lat_a, id_a in nodes:
        row = []
        for idx_b, lon_b, lat_b, id_b in nodes:
            if idx_a == idx_b or id_a == id_b:
                row.append(str(0.0))
            else:
                row.append(str(cost_matrix[id_a][id_b][1]))
        lines.append(' '.join(row))

    lines.append('DEMAND_SECTION')
    idx = 0
    for id, (lat, lon) in enumerators.items():
        lines.append('{} {}'.format(idx, 0))
        idx += 1

    for id, (lat, lon) in locations.items():
        lines.append('{} {}'.format(idx, 1))
        idx += 1

    lines.append('DEPOT_SECTION')
    idx = 0
    for id, (lat, lon) in enumerators.items():
        lines.append('{}'.format(idx))
        idx += 1
    lines.append('-1')

    lines.append('EOF')

    f.writelines('\n'.join(lines))