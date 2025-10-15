# Mint Initialization

This document describes the process of initializing mints from the Numista website into the database.

## Overview

The mint initialization system parses the Numista mints catalog page and saves all mint information to the Neo4j database.

## Source

The mints are parsed from: `https://en.numista.com/catalogue/mints.php`

## Data Structure

Each mint entry contains:
- **nid**: Mint ID from Numista (extracted from the URL `/catalogue/mint.php?id=XXX`)
- **fullName**: Full name of the mint (extracted from `<strong>` tag)
- **latitude**: Geographic latitude (extracted from `map.flyTo([lat, lon])` in the onclick attribute)
- **longitude**: Geographic longitude (extracted from `map.flyTo([lat, lon])` in the onclick attribute)

## Components

### 1. Entity: `Mint.java`
Location: `src/main/java/bkv/colligendis/database/entity/numista/Mint.java`

The Mint entity stores mint information with the following key properties:
- `nid` - Numista ID
- `fullName` - Full mint name
- `latitude` - Geographic coordinate (latitude)
- `longitude` - Geographic coordinate (longitude)
- Additional legacy properties (name, place, operationStartYear, etc.)

### 2. Parser: `NumistaAllMintsParser.java`
Location: `src/main/java/bkv/colligendis/utils/numista/mint/NumistaAllMintsParser.java`

The parser:
1. Loads the mints catalog page from Numista
2. Parses each table row containing mint information
3. Extracts:
   - Mint ID from links with format `/catalogue/mint.php?id=XXX`
   - Full name from `<strong>` tags
   - Coordinates from `<a href="#map_canvas">` onclick attribute containing `map.flyTo([lat, lon])`
4. Saves or updates mint entities in the database

### 3. Service: `MintService.java`
Location: `src/main/java/bkv/colligendis/database/service/numista/MintService.java`

The service provides:
- `initMints()` - Triggers the parsing and initialization process
- `findByNid(String nid, String fullName)` - Finds or creates mint by ID
- Other repository methods for mint management

### 4. REST Controller: `MintRestController.java`
Location: `src/main/java/bkv/colligendis/rest/MintRestController.java`

Provides HTTP endpoints:
- `GET /database/mint/init` - Initialize/update all mints from Numista
- `GET /database/mint/all` - Retrieve all mints from database

## Usage

### Initialize Mints

To initialize or update mints from Numista:

```http
GET http://localhost:8080/database/mint/init
Accept: application/json
```

Response:
```json
{
  "data": "Mints initialized successfully",
  "message": "Mints parsed and saved from Numista",
  "status": "SUCCESS"
}
```

### Get All Mints

To retrieve all mints from the database:

```http
GET http://localhost:8080/database/mint/all
Accept: application/json
```

Response:
```json
{
  "data": [
    {
      "uuid": "...",
      "nid": "123",
      "fullName": "Moscow Mint",
      "latitude": "55.755833",
      "longitude": "37.617778",
      ...
    },
    ...
  ],
  "message": "Mints fetched successfully",
  "status": "SUCCESS"
}
```

## Process Flow

1. User calls `/database/mint/init` endpoint
2. `MintRestController` receives the request
3. `MintService.initMints()` is called
4. `NumistaAllMintsParser.parseAndSaveAllMints()` executes:
   - Loads the Numista mints page
   - Parses all table rows
   - For each mint:
     - Extracts ID, name, and coordinates
     - Checks if mint exists in database
     - Creates new mint or updates existing one
     - Saves to Neo4j database
5. Returns success/error response

## Error Handling

The parser includes error handling for:
- Failed page loads
- Missing or invalid mint data
- Parsing errors for individual mints
- Database connection issues

Errors are logged using `DebugUtil` and the process continues with remaining mints.

## Database Updates

- If a mint with the same `nid` exists, its `fullName` and coordinates are updated
- New mints are created if they don't exist
- The parser uses `MintService.findByNid()` which automatically creates or updates mints

## Testing

Use the provided HTTP requests file: `mint_requests.http`

```http
### Initialize all mints from Numista
GET http://localhost:8080/database/mint/init
Accept: application/json

### Get all mints
GET http://localhost:8080/database/mint/all
Accept: application/json
```

## Notes

- The parser requires authentication cookies defined in `NumistaPartParser.COOKIE`
- Coordinates are only available for mints that have location data on Numista
- The parsing process may take some time depending on the number of mints
- All operations are idempotent - running initialization multiple times is safe

