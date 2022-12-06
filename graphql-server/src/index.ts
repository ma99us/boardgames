import {ApolloServer} from '@apollo/server';
import {startStandaloneServer} from '@apollo/server/standalone';
import games from '../data/extract-bgg-games-details.json' assert {type: 'json'};
// import games from '../data/sample-games.json' assert {type: 'json'};


// A schema is a collection of type definitions (hence "typeDefs")
// that together define the "shape" of queries that are executed against
// your data.
const typeDefs = `#graphql
# Comments in GraphQL strings (such as this one) start with the hash (#) symbol.

# This "Game" type defines the queryable fields for every game in our data source.
type Game {
    bggId: Int,
    name: String,
    description: String,
    thumbnailUrl: String,
    imageUrl: String,
    minPlayers: Int,
    maxPlayers: Int,
    yearPublished: Int,
    publisher: String,
    designer: String,
    artist: String,
    honors: String,
    bestPlayers: [Int],
    goodPlayers: [Int],
    avgRating: Float,
    avgWeight: Float
}

# The "Query" type is special: it lists all of the available queries that
# clients can execute, along with the return type for each. In this
# case, the "games" query returns an array of zero or more Games (defined above).
type Query {
    games: [Game]
}
`;

// Resolvers define how to fetch the types defined in your schema.
// This resolver retrieves games from the "games" array above.
const resolvers = {
    Query: {
        games: () => games,
    },
    //TODO: add more resolvers to filter by 'avgRating' and 'avgWeight'
};

// The ApolloServer constructor requires two parameters: your schema
// definition and your set of resolvers.
const server = new ApolloServer({
    typeDefs,
    resolvers,
});

// Passing an ApolloServer instance to the `startStandaloneServer` function:
//  1. creates an Express app
//  2. installs your ApolloServer instance as middleware
//  3. prepares your app to handle incoming requests
const {url} = await startStandaloneServer(server, {
    listen: {port: 4000},
});

console.log(`🚀  Server ready at: ${url}`);