package com.santoprestandrea_s00007624.backend_travelmates.exception;

/**
 * EXCEPTION: UNAUTHORIZED USER
 *
 * Thrown when a user attempts an action for which they don't have permissions.
 *
 * EXAMPLES:
 * - A PARTICIPANT tries to modify the trip (only ORGANIZER can)
 * - A user tries to delete a trip they are not a member of
 * - A user tries to invite members (only ORGANIZER can)
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}