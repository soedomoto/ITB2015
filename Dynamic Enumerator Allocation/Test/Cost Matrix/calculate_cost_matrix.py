import itertools
import os
from random import randint

import requests
import unicodecsv as csv


# Temp: AIzaSyB0QWvuXKGaMrtO2bOMbTYHT9qSPCfdbJA
# Lutfi: AIzaSyAiUfplvb0sY0sfwEfn5bj2b_mN2mnOzII
# Bayu: AIzaSyCnSxiOznT8VZflG38OOHYvGA5bl5pzT0U
# Ardi: AIzaSyA4cqRJNjYAE3KWfBrb0zt0tU1J_LBQeUU
# Netz: AIzaSyA1qRZoih9Hu4KWJOchOT0ozRng2nKmgKg

API_KEYS = ['AIzaSyB0QWvuXKGaMrtO2bOMbTYHT9qSPCfdbJA', 'AIzaSyAiUfplvb0sY0sfwEfn5bj2b_mN2mnOzII',
            'AIzaSyCnSxiOznT8VZflG38OOHYvGA5bl5pzT0U', 'AIzaSyA4cqRJNjYAE3KWfBrb0zt0tU1J_LBQeUU',
            'AIzaSyA1qRZoih9Hu4KWJOchOT0ozRng2nKmgKg', 'AIzaSyDw81cjBkvmY90UUPUs9U8_GpT9wfN5Y94',
            'AIzaSyAonhyWmZ1VvWxDXVbRRGe66JpC32Ti05E']
CURR_KEY = 0

input = 'coord_nagari_2014.csv'
output = 'cost_matrix_nagari_2014.csv'



def get_distance_duration(lat_a, lon_a, lat_b, lon_b, times=1):
    global API_KEYS
    global CURR_KEY

    resp = requests.get('https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins={},{}'
                        '&destinations={},{}&key={}'.format(lat_a, lon_a, lat_b, lon_b, API_KEYS[CURR_KEY]))

    result = resp.json()
    if result['status'] == 'OVER_QUERY_LIMIT':
        if times >= (2*len(API_KEYS)):
            return None, None, 'OVER_QUERY_LIMIT'

        CURR_KEY = randint(0, len(API_KEYS) - 1)
        return get_distance_duration(lat_a, lon_a, lat_b, lon_b, times+1)

    else:
        distance = result['rows'][0]['elements'][0]['distance']['value']
        duration = result['rows'][0]['elements'][0]['duration']['value']

        return distance, duration, result['status']



input_file = open(os.path.join(os.pardir, 'Data', input), 'r')
output_file = open(os.path.join(os.pardir, 'Data', output), 'r')

last_calc = {}
try:
    for loc_a, loc_b, distance, duration, status in csv.reader(output_file):
        if (duration and duration != "None") or (status and status == 'OK'):
            last_calc[(loc_a, loc_b)] = (distance, duration, status)
except Exception, e: pass


output_file = open(os.path.join(os.pardir, 'Data', output), 'wb')

csv.writer(output_file, encoding='utf-8').writerow(('id_a', 'id_b', 'distance', 'duration', 'status'))
output_file.flush()

coords = {}
for id, lat, lon in list(csv.reader(input_file, encoding='utf-8'))[1:]:
    coords[id] = (lat, lon, )

bi_locs = itertools.combinations(coords.keys(), 2)
bi_locs = list(bi_locs)
for idx, (loc_a, loc_b) in enumerate(bi_locs):
    lat_a, lon_a = coords[loc_a]
    lat_b, lon_b = coords[loc_b]

    distance = None
    duration = None
    status = ''

    if (loc_a, loc_b) not in last_calc:
        try:
            distance, duration, status = get_distance_duration(lat_a, lon_a, lat_b, lon_b)
        except Exception, e:
            pass

    else:
        distance, duration, status = last_calc[(loc_a, loc_b)]

    print('{}/{} Processing {},{} to {},{} ==> {},{} {}'.format(idx + 1, len(bi_locs), lat_a, lon_a, lat_b, lon_b,
                                                                distance, duration, status))

    csv.writer(output_file, encoding='utf-8').writerow((loc_a, loc_b, str(distance), str(duration), status))
    output_file.flush()
