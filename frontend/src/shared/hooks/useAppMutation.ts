import { useMutation, type UseMutationOptions, type UseMutationResult } from '@tanstack/react-query';
import { AppError } from '../../api/errorParser';

export function useAppMutation<TData, TVariables>(
    options: UseMutationOptions<TData, AppError, TVariables>
): UseMutationResult<TData, AppError, TVariables> {
    return useMutation<TData, AppError, TVariables>(options);
}