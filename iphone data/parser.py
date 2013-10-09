import re,os

os.chdir('/Users/Daneeq/Documents/code/StrideHack/iphone data')

# switch
sample = True

# 1: location
# 7: braking
# 6 acceleration

# if sample == True:
# 	locPath = 'iphone data/1.xml'
# else:
# 	locPath = 'data/londonLocationData.txt'

locFile = '1.xml'
accFile = '6.xml'
brakFile = '7.xml'

latVals = []
lonVals = []
brakVals = []
accVals = []

path = locFile

with open(path) as data_file:
		count = 0
		for line in data_file:
			line = line.strip()
			if "value" in line:
				clean = re.sub('<[^<]+?>','',line)
				[lat, lon] = clean.split()
				latVals.append(lat)
				lonVals.append(lon)
			count += 1
		size = count

		

path = accFile

with open(path) as data_file:
		i = 0
		for line in data_file:
			line = line.strip()
			if "value" in line:
				clean = re.sub('<[^<]+?>','',line)
				accVals.append(clean)

path = brakFile

with open(path) as data_file:
		i = 0
		for line in data_file:
			if "value" in line:
				line = line.strip()
				clean = re.sub('<[^<]+?>','',line)
				brakVals.append(clean)
				# if clean != 0.0:
				# 	print("NOT ZERO")

print(len(lonVals))
print(len(latVals))
print(len(brakVals))
print(len(accVals))

f = open('data.txt', 'w')

for i in range(700):
	f.write(latVals[i] + ',' + lonVals[i] + ',' + brakVals[i] + ',' + accVals[i] + '\n')
